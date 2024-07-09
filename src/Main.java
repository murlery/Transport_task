import java.util.Arrays;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] post = {180, 90, 120}; // Поставки
        int[] potr = {155, 80, 75, 29}; // Потребности
        int[] post0 = {180, 90, 120};
        int[] potr0 = {155, 80, 75, 29};
        int[][] values = {
                {15, 3, 6, 12},
                {6, 15, 30, 9},
                {30, 6, 6, 15}
        }; // Матрица тарифов

        if (sumMas(post) > sumMas(potr)) {

            values = addColumnToValues(values, new int[]{0, 0, 0});

        } else if (sumMas(post) < sumMas(potr)) {
            values = addRowToValues(values, new int[]{0, 0, 0, 0});
        }

        int[][] plan = solveTransportTask(post, potr, values);
        // Вывод опорного плана
        printPlan(plan, post0, potr0);
        // Проверка на оптимальность методом потенциалов
        if (isPlanOptimal(plan, values)) {
            System.out.println("Опорный план оптимален.");
        } else {
            System.out.println("Опорный план не оптимален.");
            plan = makePlanOptimal(plan, values); // Обновляем план
            System.out.println("Оптимальный план:");
            printPlan(plan, post0, potr0); // Выводим оптимальный план
        }
    }

    // Функция для добавления строки в матрицу values
    public static int[][] addRowToValues(int[][] values, int[] newRow) {
        int rows = values.length;
        int cols = values[0].length;
        int[][] newValues = new int[rows + 1][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(values[i], 0, newValues[i], 0, cols);
        }
        System.arraycopy(newRow, 0, newValues[rows], 0, cols);
        return newValues;
    }

    // Функция для добавления столбца в матрицу values
    public static int[][] addColumnToValues(int[][] values, int[] newColumn) {
        int rows = values.length;
        int cols = values[0].length;
        int[][] newValues = new int[rows][cols + 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newValues[i][j] = values[i][j];
            }
            newValues[i][cols] = newColumn[i];
        }
        return newValues;
    }
// ... (остальной код функций solveTransportTask, isPlanOptimal, makePlanOptimal, findCycle)

    // Функция для вывода плана
    public static void printPlan(int[][] plan, int[] post, int[] potr) {
        for (int i = 0; i < plan.length; i++) {
            for (int j = 0; j < plan[i].length; j++) {
                System.out.print(plan[i][j] + "\t");
            }
            System.out.println("  |  " + post[i]);
            System.out.println("-----------------------------------");
        }
        for (int i = 0; i < potr.length; i++) {
            System.out.print(potr[i] + "\t");
        }
        System.out.println();
    }

    public static int sumMas(int[] mas) {
        int sum = 0;
        for (int i = 0; i < mas.length; i++) {
            sum += mas[i];
        }
        return sum;
    }

    public static int[] addEl(int[] mas, int a, int b) {
        int[] mas1 = new int[mas.length + 1];
        for (int i = 0; i < mas.length; i++) {
            mas1[i] = mas[i];
        }
        mas1[mas.length] = a - b;
        return mas1;
    }

    public static int[][] solveTransportTask(int[] post, int[] potr, int[][] values) {

        if (sumMas(post) == sumMas(potr)) {
            int rows = post.length;
            int cols = potr.length;
            int[][] plan = new int[rows][cols];

            int i = 0; // Индекс строки
            int j = 0; // Индекс столбца

            while (i < rows && j < cols) {
                if (post[i] <= potr[j]) {
                    plan[i][j] = post[i];
                    potr[j] -= post[i];
                    i++;
                } else {
                    plan[i][j] = potr[j];
                    post[i] -= potr[j];
                    j++;
                }
            }

            return plan;
        } else if (sumMas(post) > sumMas(potr)) {
            int[] mas = addEl(potr, sumMas(post), sumMas(potr));
            int rows = post.length;
            int cols = mas.length;
            int[][] plan = new int[rows][cols];
            int i = 0; // Индекс строки
            int j = 0; // Индекс столбца

            while (i < rows && j < cols) {
                if (post[i] <= mas[j]) {
                    plan[i][j] = post[i];
                    mas[j] -= post[i];
                    i++;
                } else {
                    plan[i][j] = mas[j];
                    post[i] -= mas[j];
                    j++;
                }
            }
            return plan;
        } else {
            int[] mas = addEl(post, sumMas(potr), sumMas(post));
            int rows = mas.length;
            int cols = potr.length;
            int[][] plan = new int[rows][cols];
            int i = 0; // Индекс строки
            int j = 0; // Индекс столбца

            while (i < rows && j < cols) {
                if (mas[i] <= potr[j]) {
                    plan[i][j] = mas[i];
                    potr[j] -= mas[i];
                    i++;
                } else {
                    plan[i][j] = potr[j];
                    mas[i] -= potr[j];
                    j++;
                }
            }
            return plan;
        }
    }

    public static boolean isPlanOptimal(int[][] plan, int[][] values) {
        int rows = plan.length;
        int cols = plan[0].length;

        // Матрица потенциалов
        double[] u = new double[rows];
        double[] v = new double[cols];

        // Инициализация потенциалов
        u[0] = 0; // Первый потенциал равен 0
        for (int j = 0; j < cols; j++) {
            if (plan[0][j] > 0) {
                v[j] = values[0][j] - u[0];
            }
        }

        // Вычисление остальных потенциалов
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (plan[i][j] > 0) {
                    u[i] = values[i][j] - v[j];
                    break;
                }
            }
        }
      /*  System.out.println("Потенциалы:");
        System.out.print("u: ");
        for (int i = 0; i < rows; i++) {
            System.out.print(u[i] + " ");
        }
        System.out.println();
        System.out.print("v: ");
        for (int j = 0; j < cols; j++) {
            System.out.print(v[j] + " ");
        }
        System.out.println();*/

        // Проверка условий оптимальности
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (plan[i][j] == 0 && (values[i][j] - u[i] - v[j]) > 0) {
                    return false; // План не оптимален, если найдено ненулевое значение (values[i][j] - u[i] - v[j])
                }
            }
        }
        return true; // План оптимален
    }

    public static int[][] makePlanOptimal(int[][] plan, int[][] values) {
        int rows = plan.length;
        int cols = plan[0].length;
        // Матрица потенциалов
        double[] u = new double[rows];
        double[] v = new double[cols];
        // Инициализация потенциалов
        u[0] = 0; // Первый потенциал равен 0
        for (int j = 0; j < cols; j++) {
            if (plan[0][j] > 0) {
                v[j] = values[0][j] - u[0];
            }
        }
        // Вычисление остальных потенциалов
        for (int i = 1; i < rows; i++) {
            boolean foundNonZeroElement = false;
            for (int j = 0; j < cols; j++) {
                if (plan[i][j] > 0) {
                    u[i] = values[i][j] - v[j];
                    foundNonZeroElement = true;
                    break;
                }
            }
            if (!foundNonZeroElement) {
                // Если в строке i нет ни одного ненулевого элемента, присваиваем u[i] = 0
                u[i] = 0;
            }
        }

        // Вывод потенциалов
        System.out.println("Потенциалы:");
        System.out.print("u: ");
        for (int i = 0; i < rows; i++) {
            System.out.print(u[i] + " ");
        }
        System.out.println();
        System.out.print("v: ");
        for (int j = 0; j < cols; j++) {
            System.out.print(v[j] + " ");
        }
        System.out.println();

        // Проверка на оптимальность плана
        if (isPlanOptimal(plan, values)) {
            return plan; // План уже оптимален
        }

        // Поиск ячейки с максимальным потенциалом
        int maxI = 0;
        int maxJ = 0;
        double maxPot = Double.MIN_VALUE;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (plan[i][j] == 0 && (values[i][j] - u[i] - v[j]) > maxPot) {
                    maxI = i;
                    maxJ = j;
                    maxPot = values[i][j] - u[i] - v[j];
                }
            }
        }

        // Поиск цикла
        int[][] cycle = findCycle(plan, maxI, maxJ);

        // Обновление плана
        if (cycle.length > 0) {
            int minVal = Integer.MAX_VALUE;
            for (int i = 0; i < cycle.length; i++) {
                if (plan[cycle[i][0]][cycle[i][1]] > 0 && plan[cycle[i][0]][cycle[i][1]] < minVal) {
                    minVal = plan[cycle[i][0]][cycle[i][1]];
                }
            }
            for (int i = 0; i < cycle.length; i++) {
                if (i % 2 == 0) {
                    plan[cycle[i][0]][cycle[i][1]] += minVal;
                } else {
                    plan[cycle[i][0]][cycle[i][1]] -= minVal;
                }
            }
        }
        return plan;
    }

    public static int[][] findCycle(int[][] plan, int maxI, int maxJ) {
        int rows = plan.length;
        int cols = plan[0].length;
        // Начальный размер массива cycle установлен на 4
        int[][] cycle = new int[rows][cols];
        int cycleSize = 0;
        int currentI = maxI;
        int currentJ = maxJ;
        cycle[cycleSize][0] = currentI;
        cycle[cycleSize][1] = currentJ;
        cycleSize++;
        while (currentI != maxI || currentJ != maxJ) {
            // Поиск следующей ячейки в цикле
            int nextI = -1;
            int nextJ = -1;
            for (int j = 0; j < cols; j++) {
                if (plan[currentI][j] > 0 && (currentJ != j || (currentI != maxI || currentJ != maxJ))) {
                    nextI = currentI;
                    nextJ = j;
                    break;
                }
            }
            if (nextI == -1 && nextJ == -1) {
                for (int i = 0; i < rows; i++) {
                    if (plan[i][currentJ] > 0 && (currentI != i || (currentI != maxI && currentJ != maxJ))) {
                        nextI = i;
                        nextJ = currentJ;
                        break;
                    }
                }
            }
            if (nextI == -1 && nextJ == -1) {
                // Цикл не найден
                return new int[0][0];
            }
            currentI = nextI;
            currentJ = nextJ;
            // Проверка на выход за границы массива cycle
            if (cycleSize == cycle.length) {
                // Увеличение размера массива cycle в 2 раза
                cycle = Arrays.copyOf(cycle, cycle.length * 2);
            }
            if (cycleSize < cycle.length) {
                cycle[cycleSize][0] = currentI;
                cycle[cycleSize][1] = currentJ;
                cycleSize++;
            } else {
                // Если массив cycle заполнен, выходим из цикла
                break;
            }
        }
        // Возвращаем цикл с правильным размером
        return Arrays.copyOf(cycle, cycleSize);
    }
}