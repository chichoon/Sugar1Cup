csvlist = dir('C:\Users\jiyoo\Documents\code\binfiles\*/*.csv');
N = size(csvlist, 1);
arrX = [];
arrY = [];
arrZ = [];
arrC = [];

for i = 1 : N
    filename = [csvlist(i).name];
    cd(csvlist(i).folder);
    
    filemat = readmatrix(filename);
    
    %indexing matrix for individual graph
    X = filemat(1:end, 10);
    Y = filemat(1:end, 11);
    Z = filemat(1:end, 12);
    C = filemat(1:end, 10:12) / 255;
    
    %concatenate matrix for all-in-one graph
    arrX = vertcat(arrX, X);
    arrY = vertcat(arrY, Y);
    arrZ = vertcat(arrZ, Z);
    temp = size(X);
    temp1 = [];
    for i = 1 : 3
        temp1 = horzcat(repmat(rand(), temp(1), 1), repmat(rand(), temp(1), 1), repmat(rand(), temp(1), 1));
    end
    arrC = vertcat(arrC, temp1);
    
    %print individual graph
    scatter3(X, Y, Z, 25, C, 'filled', 'o', 'MarkerEdgeColor', 'black');
    axis([0 255 0 255 0 255]);
    title(strcat(filename, ' RGB scatter'));
    xlabel('R');
    ylabel('G');
    zlabel('B');
    saveas(gcf, strcat(strcat('00', filename), '.png')); 
end
%print all-in-one graph with randomly generated color plot
%arrC = horzcat(arrX, arrY, arrZ) / 255;
scatter3(arrX, arrY, arrZ, 25, arrC, 'filled', 'o');
axis([0 255 0 255 0 255]);
title('RGB scatter');
xlabel('R');
ylabel('G');
zlabel('B');
saveas(gcf, '0000graph.png');