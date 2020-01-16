
x = [0 2 7 8 38 408 10000];
y = [1025991, 881376, 869756, 868500, 838593, 831989, 831989];
h1 = plot(x, y, 'r-o', 'linewidth',2);
hold on

x = [0 2 3 5 22 10000];
y = [689186 615692, 613676, 602846, 591273, 591273];
h2 = plot(x, y, 'marker', '>', 'Color',[0,0.7,0.9], 'linewidth',2);
hold on

x = [0 39 76 113 212 237 246 349 482 873 932 1158 1605 10000];
y = [923697 845743 819358 806217 782612 719528 718139 705329 688074 645946 586796 565666 563959 563959];
h3 = plot(x, y, 'marker', 'p', 'color', 'blue', 'linewidth',2);
hold on

grid on 
set(gca,'FontSize',20)
xlabel('iterations','fontsize',24);
ylabel('#cost','fontsize',24);
legend([h1, h2, h3], {'H-FPIUS', 'H-BPIUS', 'ALNS'},'fontsize',22)
title('N_{200}','fontsize',24);