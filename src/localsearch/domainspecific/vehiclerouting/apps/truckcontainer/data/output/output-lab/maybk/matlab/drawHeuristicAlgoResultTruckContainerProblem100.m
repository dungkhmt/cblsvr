
x = [0 2 5 9 10000];
y = [567468, 495195, 465740, 436860, 436860];
h1 = plot(x, y, 'r-o', 'linewidth',2);
hold on
 
x = [0 4 10 25 30 10000];
y = [365334, 337291, 326087, 325933, 303323, 303323];
h2 = plot(x, y, 'marker', '>', 'Color',[0,0.7,0.9], 'linewidth',2);
hold on

x = [0 23 214 272 392 434 447 557 2212 2215 10000];
y = [509808, 371078, 365931, 315422, 310015, 293729, 287873, 287154, 270508, 264912, 264912];
h3 = plot(x, y, 'marker', 'p', 'color', 'blue', 'linewidth',2);
hold on

grid on 
set(gca,'FontSize',20)
xlabel('iterations','fontsize',24);
ylabel('#cost','fontsize',24);
legend([h1, h2, h3], {'H-FPIUS', 'H-BPIUS', 'ALNS'},'fontsize',22)
title('N_{100}','fontsize',24);
