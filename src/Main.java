import java.util.concurrent.atomic.AtomicInteger;
class InterferenceExample {
    private static final int HUNDRED_MILLION = 100_000_000;//сколько раз надо прибавить 1
    private static final AtomicInteger counter = new AtomicInteger();// атомарный счётчик, используем как стоп-сигнал.Безопасен для потоков, цикл выполнится ровно 100 млн раз
    private static int i;// общ переменная, не синхронизируется. Потоки меняют её одновременно. Некоторые прибавки потеряются из за гонок потоков

    static boolean shouldStop() {//проверка надо ли остановиться
        return counter.incrementAndGet() > HUNDRED_MILLION;
    }
    static class InterferenceThread extends Thread { //поток добавляет 1 к i
        @Override
        public void run() { //выполняется при вызове start()
            //  цикл прервется только когда shouldStop вернет true.операция не атомарная
            while (!shouldStop()) {// если два потока прочитают одно и то же значение, оба запишут одинаковый результат, и одна прибавка пропадёт
                i++; //прибавляем 1
            }
        }
    }

    public static class Main {
        static void main() throws InterruptedException {
            // Создаём два потока, которые работают с одной и той же переменной i
            InterferenceThread thread1 = new InterferenceThread();
            InterferenceThread thread2 = new InterferenceThread();
            long startTime = System.currentTimeMillis();

            thread1.start();//запускаем
            thread2.start();//потоки работают одновременно

            thread1.join();//ждем пока оба потока закончат работу
            thread2.join();
            long endTime = System.currentTimeMillis();

            System.out.println("Ожидаемый результат: " + HUNDRED_MILLION); //100 млн
            System.out.println("Фактический результат: " + i);//фактич результат меньше из за гонок потоков
            System.out.println("Потеряно: " + (HUNDRED_MILLION - i));//разница фактич и ожидаемого результата (сколько прибавок было потеряно)
            System.out.println("Время: " + (endTime - startTime) + " мс");
        }
    }
}


