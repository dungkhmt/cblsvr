
x = [0 1 100001];
y = [86430 57784 57784];
h1 = plot(x, y, '-.r*', 'linewidth',2);
hold on
 
x = [0 7 55 67 100001];
y = [86430 60435 59288 55662 55662];
h3 = plot(x, y, 'marker', '>', 'Color',[0,0.7,0.9], 'linewidth',2);
hold on

x = [0 1001 2002 100001];
y = [86430 76764 70316 70316];
h4 = plot(x, y, 'marker', 'p', 'color', 'blue', 'linewidth',2);
hold on

x = [0 1001 2002 100001];
y = [86430 76764 70316 70316];
h2 = plot(x, y, '--mo', 'linewidth',2);
hold on

x = [0 1 100001];
y = [86430 67330 67330];
h5 = plot(x, y, ':bs', 'linewidth',2);
hold on

x = [0 10 69 187 192 1150 100001];
y = [86430 70730 70557 70209 68691 68066 68066];
h7 = plot(x, y, 'marker', '^', 'Color',[0 1 0], 'linewidth',2);
hold on

x = [0 6 8 100001];
y = [86430 76419 76032 76032];
h8 = plot(x, y, '-.h', 'color', [0.3010 0.7450 0.9330], 'linewidth',2);
hold on

x = [0 5 100001];
y = [86430 86285 86285];
h6 = plot(x, y, 'marker', 'v', 'color', [0.9290 0.6940 0.1250], 'linewidth',2);
hold on



grid on 
set(gca,'FontSize',20)
xlabel('iterations','fontsize',24);
ylabel('#cost','fontsize',24);
legend([h1, h2, h3, h4, h5, h6, h7, h8], {'I1', 'I2', 'I3','I4','I5','I6','I7','I8'},'fontsize',22)
title('N_{20}','fontsize',24);
