x = [0 3 4 13 17 34 38 71 1000];
y = [56141, 42882, 40955, 40020, 38985, 37438, 35199, 34247, 34247];
h1 = plot(x, y, 'r', 'linewidth',2);
hold on

x = [0 5 16 51 134 663 709 892 1000];
y = [56141, 37369, 36659, 34759, 34567, 34383, 34339, 34247, 34247];
h2 = plot(x, y, 'blue', 'linewidth',2);
hold on

grid on 
xlabel('iterations');
ylabel('#cost');
legend([h1, h2], {'H-FPIUS', 'H-BPIUS'})
title('N_8');



