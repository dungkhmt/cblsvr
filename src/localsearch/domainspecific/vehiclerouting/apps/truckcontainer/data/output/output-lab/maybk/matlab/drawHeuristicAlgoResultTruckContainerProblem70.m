x = [0 9 10 21 67 72 120 174 710 10000];
y = [302198, 295737, 276176, 271826, 264063, 263078, 256337, 254800, 205444, 205444];
h1 = plot(x, y, 'r-o', 'linewidth',2);
hold on

x = [0 2 4 11 58 190 10000];
y = [217465, 211989, 195077, 188834, 179359, 168155, 168155];
h2 = plot(x, y, 'marker', '>', 'Color',[0,0.7,0.9], 'linewidth',2);
hold on

x = [0 21 53 87 237 973 990 1133 1384 2834 3844 4572 10000];
y = [298848, 232009, 178014, 171648, 169683, 167161, 163686,163435,158460,155898,155553,153802,153802];
h3 = plot(x, y, 'marker', 'p', 'color', 'blue', 'linewidth',2);
hold on

grid on 
set(gca,'FontSize',20)
xlabel('iterations','fontsize',24);
ylabel('#cost','fontsize',24);
legend([h1, h2, h3], {'H-FPIUS', 'H-BPIUS', 'ALNS'},'fontsize',22)
title('N_{70}','fontsize',24);