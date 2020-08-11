csvlist = dir('C:\Users\jiyoo\Documents\code\binfiles\*/*.csv');
N = size(csvlist, 1);

for i = 1 : N
    filename = [csvlist(i).name];
    cd(csvlist(i).folder);
    
    filemat = readmatrix(filename);
    
    %indexing matrix for individual graph
    X = filemat(1:end, 2);
    Y = filemat(1:end, 3);
    Z = filemat(1:end, 4);
    
    %print individual graph
    scatter3(X, Y, Z, 25, 'filled', 'o');
    axis([-16000 5000 -2500 4000 0 17000]);
    title(strcat(filename, ' Accelo scatter'));
    xlabel('AccelX');
    ylabel('AccelY');
    zlabel('AccelZ');
    saveas(gcf, strcat(strcat('11', filename), '.png')); 
end