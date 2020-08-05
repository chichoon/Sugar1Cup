cd 'C:\Users\jiyoo\Documents\code\summer\bin'
dir **/*.bin

csvlist = dir('C:\Users\jiyoo\Documents\code\summer\bin\*.csv');
N = size(csvlist, 1);
arrX = [];
arrY = [];
arrZ = [];
arrC = [];

for i = 1 : N
    filename = [csvlist(i).name];
    filemat = readmatrix(filename);
    
    %개별 그래프 출력을 위한 행렬 합치기
    X = filemat(1:end, 10);
    Y = filemat(1:end, 11);
    Z = filemat(1:end, 12);
    C = filemat(1:end, 10:12) / 255;
    
    %한번에 그래프 출력을 위한 행렬 concatenate
    arrX = vertcat(arrX, X);
    arrY = vertcat(arrY, Y);
    arrZ = vertcat(arrZ, Z);
    temp = size(X);
    temp1 = [];
    for i = 1 : 3
        temp1 = horzcat(repmat(rand(), temp(1), 1), repmat(rand(), temp(1), 1), repmat(rand(), temp(1), 1));
    end
    arrC = vertcat(arrC, temp1);
    
    %개별 그래프 출력
    scatter3(X, Y, Z, 25, C, '*');
    axis([0 255 0 255 0 255]);
    saveas(gcf, strcat(strcat('00', filename), '.png')); 
end
%arrC = horzcat(arrX, arrY, arrZ) / 255;
scatter3(arrX, arrY, arrZ, 25, arrC, '*');
axis([0 255 0 255 0 255]);
saveas(gcf, '0000graph.png'); %합쳐진 그래프 출력