
import sys
import os
from PyQt5.QtGui import QIcon
from PyQt5.QtCore import pyqtSignal, QObject, QThread
from PyQt5.QtWidgets import QErrorMessage, QApplication, QWidget, QDesktopWidget
from PyQt5.QtWidgets import QLabel, QHBoxLayout, QVBoxLayout, QMainWindow, QLineEdit
from PyQt5.QtWidgets import QPushButton, QAction, QMenu, QSystemTrayIcon, qApp, QWidget
from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer
import time
import binkeras

direction = 'C:/Users/jiyoo/Documents/code/binfiles'
expl = '내 binary 파일을 감시합니다.'
newfile = 0 #number of new files updated after last training
accuracyN = 0 #accuracy of last training
costN = 0 #cost of last training
trainflag = False #whether it is training or not
newflag = False


class Singleton(object): #allows only one process working
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not isinstance(cls._instance, Singleton):
            cls._instance = object.__new__(cls, *args, **kwargs)
        return cls._instance
        

class binfileWatch(Singleton):
    def __init__(self):
        self.observer = None
        self.is_watching = False


    def setting(self):
        self.observer = Observer() #define observer for watchdog
        eventhandler = Handler(self.observer)
        self.observer.schedule(eventhandler, direction, recursive=True)
        

    def run(self): #start observing
        if not self.observer:
            self.setting()
        self.observer.start()
        self.is_watching = True


    def stop(self): #stop observing
        self.observer.stop()
        self.observer = None
        self.is_watching = False

    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.stop()


class Handler(FileSystemEventHandler):
    def __init__(self, observer):
        self.observer = observer
        self.dirpath = direction #direction to watch file creation
        self.tag = 'MyEventHandler'
        self.wait = 1
        self.retry = 10
        
    
    def on_created(self, event): #executed when file is created in folder
        global newflag
        global newfile
        newfile += 1
        newflag = True


class watchThread(QThread): #looping in GUI program
    newfilesignal = pyqtSignal(int)
    newfiletrainsignal = pyqtSignal(int)
    def __init__(self):
        QThread.__init__(self)
    
    def run(self):
        global newflag, trainflag
        global newfile
        while True:
            if newflag : #check flag (global variable) to see if new file is created
                if not trainflag:
                    self.newfilesignal.emit(newfile)
                    newflag = False
                    trainflag = True
                    self.usleep(100)
                else:
                    self.newfiletrainsignal.emit(newfile)
                    newflag = False
                    self.usleep(100)



class trainThread(QThread):
    trainsignal = pyqtSignal(int)
    traindonesignal = pyqtSignal(int)
    def __init__(self):
        QThread.__init__(self)

    def run(self):
        global trainflag, newflag
        global accuracyN, costN, newfile
        while True:
            if trainflag:
                self.sleep(1)
                self.trainsignal.emit(newfile) #emit signal if so
                newfile = 0
                datax, datay = binkeras.createdata(direction)
                costN, accuracyN = binkeras.func_keras(datax, datay)
                self.traindonesignal.emit(newfile)
                newflag = False
                trainflag = False
                self.sleep(1)



class App(QMainWindow):
    tray_icon = None
    def __init__(self):
        super().__init__()

        self.widget = QWidget()
        self.ledt = QLineEdit()
        self.watchthread = watchThread()
        self.trainthread = trainThread()
        self.watch = binfileWatch()
        self.initUI()
        self.initActions()
        self.setFixedSize(400, 200)
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        pass

    def initUI(self):
        global newfile

        self.layout1 = QHBoxLayout() #Program Description
        self.layout2 = QHBoxLayout() #number of new files updated, now training...
        self.layout3 = QHBoxLayout() #model Accuracy & Cost
        self.layout4 = QHBoxLayout() #button - watchdog start, stop
        self.layout5 = QHBoxLayout() #button - watchdog quit
        self.main_layout = QVBoxLayout() #layout for all-in-one

        self.explabel = QLabel(''.join([os.path.splitext(os.path.basename(direction))[0], ' 폴더 내 binary 파일을 감시합니다.']))
        self.newlabel = QLabel(''.join([str(newfile), ' 개의 신규 파일 존재']))
        self.acclabel = QLabel(''.join(['Model Accuracy : ', str(accuracyN), ' Model Cost : ', str(costN)]))
        self.startbtn = QPushButton('감시 시작')
        self.quitbtn = QPushButton('프로그램 종료')

        self.layout1.addWidget(self.explabel)
        self.layout2.addWidget(self.newlabel)
        self.layout3.addWidget(self.acclabel)
        self.layout4.addWidget(self.startbtn)
        self.layout5.addWidget(self.quitbtn)
        self.main_layout.addLayout(self.layout1)
        self.main_layout.addLayout(self.layout2)
        self.main_layout.addLayout(self.layout3)
        self.main_layout.addLayout(self.layout4)
        self.main_layout.addLayout(self.layout5)
        self.widget.setLayout(self.main_layout)
        self.setCentralWidget(self.widget)

        self.tray_icon = QSystemTrayIcon(self)
        self.tray_icon.setIcon(QIcon('icon.ico'))  
        show_action = QAction("Show", self)
        quit_action = QAction("Exit", self)
        hide_action = QAction("Hide", self)
        show_action.triggered.connect(self.show)
        hide_action.triggered.connect(self.hide)
        quit_action.triggered.connect(qApp.quit)
        tray_menu = QMenu()
        tray_menu.addAction(show_action)
        tray_menu.addAction(hide_action)
        tray_menu.addAction(quit_action)
        self.tray_icon.setContextMenu(tray_menu)
        self.tray_icon.show()

        self.watchthread.newfilesignal.connect(self.showMsgnewfile)
        self.watchthread.newfiletrainsignal.connect(self.showMsgnewfiletrain)
        self.trainthread.trainsignal.connect(self.showMsgtrain)
        self.trainthread.traindonesignal.connect(self.showMsgtrainDone)

        self.setWindowTitle('BinWatchdog')
        self.setWindowIcon(QIcon('icon.png'))
        self.setGeometry(0, 0, 300, 200)  
        self.show()

    
    def initActions(self):
        self.startbtn.clicked.connect(self.toggleStart)
        self.quitbtn.clicked.connect(self.toggleQuit)


    def closeEvent(self, event):
        event.ignore()
        self.hide()
        self.tray_icon.showMessage(
            'BinWatchDog',
            'Watchdog in Tray',
            QIcon('icon.ico'),
            2000)
            

    def showMsgnewfile(self, val): #executes if gain signal from thread
        self.tray_icon.showMessage(
            'BinWatchDog',
            'New item updated in folder',
            QIcon('icon.ico'),
            2000)
        self.newlabel.setText(''.join([str(val), ' 개의 신규 파일 존재'])) #update text


    def showMsgnewfiletrain(self, val): #executes if gain signal from thread
        self.tray_icon.showMessage(
            'BinWatchDog',
            'New item updated in folder',
            QIcon('icon.ico'),
            2000)
        self.newlabel.setText(''.join([str(val), ' 개의 신규 파일 존재, Training...'])) #update text


    def showMsgtrain(self, val): #executes if gain signal from thread
        self.tray_icon.showMessage(
            'BinWatchDog',
            'Training Start....',
            QIcon('icon.ico'),
            2000)
        self.newlabel.setText(''.join([str(val), ' 개의 신규 파일 존재, Training...']))


    def showMsgtrainDone(self, val): #executes if gain signal from thread
        self.tray_icon.showMessage(
            'BinWatchDog',
            'Training Done',
            QIcon('icon.ico'),
            2000)
            
        self.acclabel.setText(''.join(['Model Accuracy : ', str(round(float(accuracyN), 2)), ' Model Cost : ', str(round(float(costN), 2))]))
        self.newlabel.setText(''.join([str(val), ' 개의 신규 파일 존재']))


            
    def toggleStart(self):
        if self.watch.is_watching: #if it is now watching
            self.watch.stop() #Watchdog stop
            self.startbtn.setText('감시 시작')

        else:
            self.watch.run()
            self.watchthread.start()
            self.trainthread.start()
            self.startbtn.setText('감시 종료')

    def toggleQuit(self):
        sys.exit()

 
if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = App()
    window.show()
    sys.exit(app.exec())

