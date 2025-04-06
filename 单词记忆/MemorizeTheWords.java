import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

@SuppressWarnings("ALL")
public class MemorizeTheWords {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("E:\\IntelliJ IDEA 2024.3\\IDEAprojects\\ForHomework\\src\\words.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("选择测试量（输入大于等于1的正整数或输入ALL）：");
        String s = SCANNER.next();
        int num;
        if ("ALL".equalsIgnoreCase(s)) {
            num = words.size();
        } else {
            num = Integer.parseInt(s);
        }

        System.out.println("选择模式（输入前面的数字）：");
        System.out.println("1. 看词义写单词");
        System.out.println("2. 看单词选词义");
        System.out.println("3. 随机速读");
        switch (SCANNER.nextInt()) {
            case 1 -> {
                while (words.size() != num) {
                    words.remove((int) (Math.random() * words.size()));
                }
                ChineseToEnglish chineseToEnglish = new ChineseToEnglish(words);
                chineseToEnglish.run();
            }
            case 2 -> {
                EnglishToChinese englishToChinese = new EnglishToChinese(words, num);
                englishToChinese.run();
            }
            case 3 -> {
                FastRun fastRun = new FastRun(words, num);
                fastRun.run();
            }
            default -> {
                System.out.println("NO~");
            }
        }
    }
}

@SuppressWarnings("ALL")
class ChineseToEnglish {
    private static boolean stopTimerThread = true;
    private static int limitTime;
    private static int score;
    private static int maxScore;
    private static String input;
    private static String[] splits;

    private List<String> words;

    private final Thread timerThread = new Thread(() -> {
        while (true) {
            if (stopTimerThread) {
                if (words.isEmpty()) {
                    break;
                }

                int random = (int) (Math.random() * words.size());
                splits = words.remove(random).split("\\|");
                for (int j = 0; j < splits.length; j++) {
                    splits[j] = splits[j].trim();
                }

                limitTime = (int) (splits[0].length() * 1.5) * 20;
                System.out.println("目前：" + (maxScore - words.size()) + " / " + maxScore);
                for (int j = 1; j < splits.length; j++) {
                    System.out.println(splits[j]);
                }
                System.out.println("请输入正确的单词，你有" + (limitTime / 20) + "秒时间：");

                stopTimerThread = false;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (splits[0].equals(input)) {
                stopTimerThread = true;
                score += 1;

                System.out.println("正确!");
                System.out.print("\n\n\n");
                continue;
            }

            limitTime -= 1;
            if (limitTime == 0) {
                stopTimerThread = true;

                System.out.println("TOO SLOW!");
                System.out.println("单词是: " + splits[0]);
                System.out.print("\n\n\n");
            }
        }

        System.out.println("你的分数是：" + score + " / " + maxScore);
        System.exit(0);
    });

    private final Thread inputThread = new Thread(() -> {
        Scanner sc = new Scanner(System.in);
        while (true) {
            input = sc.next();
        }
    });

    public ChineseToEnglish(List<String> words) {
        this.words = words;
        score = 0;
        maxScore = words.size();
    }

    public void run() {
        timerThread.start();
        inputThread.start();
    }
}

@SuppressWarnings("ALL")
class EnglishToChinese {
    private static final String[] choices = {"[A] ", "[B] ", "[C] ", "[D] ", "[E] ", "[F] ", "[G] ", "[H] "};

    private static boolean stopTimerThread = true;
    private static int num;
    private static int score;
    private static int maxScore;
    private static int limitTime;
    private static String input;
    private static String[] splits;
    private static List<Character> answer = new ArrayList<>();
    private static List<String> meaningList = new ArrayList<>();
    private static Set<Integer> usedIndex = new HashSet<>();

    private List<String> words;

    private final Thread timerThread = new Thread(() -> {
        OUT:
        while (true) {
            if (stopTimerThread) {
                if (num == 0) {
                    break;
                }

                int random = (int) (Math.random() * words.size());
                if (usedIndex.contains(random)) {
                    continue;
                }
                usedIndex.add(random);
                splits = words.get(random).split("\\|");
                for (int j = 0; j < splits.length; j++) {
                    splits[j] = splits[j].trim();
                }

                limitTime = 20 * 20;
                System.out.println("目前：" + (maxScore - num + 1) + " / " + maxScore);
                System.out.println(splits[0]);
                System.out.println("请输入该单词包含的所有词义（例如ACD），你有" + (limitTime / 20) + "秒时间：");
                List<String> choiceList = new ArrayList<>();
                boolean[] isOccupied = new boolean[8];
                for (int i = 1; i < splits.length; i++) {
                    int randomChoice = (int) (Math.random() * choices.length);
                    if (!isOccupied[randomChoice]) {
                        isOccupied[randomChoice] = true;
                        answer.add((char) ('A' + randomChoice));
                        choiceList.add(choices[randomChoice] + splits[i]);
                    } else {
                        i -= 1;
                    }
                }
                answer.sort(Comparator.naturalOrder());
                OUT2:
                while (choiceList.size() != 8) {
                    String randomMeaning = meaningList.get((int) (Math.random() * meaningList.size()));

                    for (int i = 1; i < splits.length; i++) {
                        if (randomMeaning.equals(splits[i])) {
                            continue OUT2;
                        }
                    }

                    int randomChoice = (int) (Math.random() * choices.length);
                    if (!isOccupied[randomChoice]) {
                        isOccupied[randomChoice] = true;
                        choiceList.add(choices[randomChoice] + randomMeaning);
                    }
                }

                choiceList.sort(Comparator.naturalOrder());
                for (String choice : choiceList) {
                    System.out.println(choice);
                }

                stopTimerThread = false;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (input != null) {
                List<Character> temp = new ArrayList<>();
                for (int i = 0; i < input.length(); i++) {
                    temp.add(input.charAt(i));
                }
                temp.sort(Comparator.naturalOrder());

                if (!temp.equals(answer)) {
                    System.out.println("错误!");
                    System.out.println("该单词有以下词义：");
                    for (int i = 1; i < splits.length; i++) {
                        System.out.println(splits[i]);
                    }
                    System.out.print("\n\n\n");
                } else {
                    score += 1;
                    System.out.println("正确!");
                    System.out.print("\n\n\n");
                }

                stopTimerThread = true;
                num -= 1;
                input = null;
                answer.clear();
                continue;
            }

            limitTime -= 1;
            if (limitTime == 0) {
                stopTimerThread = true;
                num -= 1;
                input = null;
                answer.clear();

                System.out.println("TOO SLOW!");
                System.out.println("该单词有以下词义：");
                for (int i = 1; i < splits.length; i++) {
                    System.out.println(splits[i]);
                }
                System.out.print("\n\n\n");
            }
        }

        System.out.println("你的分数是：" + score + " / " + maxScore);
        System.exit(0);
    });

    private final Thread inputThread = new Thread(() -> {
        Scanner sc = new Scanner(System.in);
        while (true) {
            input = sc.next().toUpperCase();
        }
    });

    public EnglishToChinese(List<String> words, int num) {
        this.words = words;
        this.num = num;
        for (String word : words) {
            String[] splits = word.split("\\|");
            for (int i = 1; i < splits.length; i++) {
                meaningList.add(splits[i].trim());
            }
        }
        score = 0;
        maxScore = num;
    }

    public void run() {
        timerThread.start();
        inputThread.start();
    }
}

class FastRun {
    private List<String> words;
    private int count;
    private int num;
    private static String[] splits;

    private final Thread fastReadThread = new Thread(() -> {
        while (count <= num) {
            try {
                int random = (int) (Math.random() * words.size());
                splits = words.remove(random).split("\\|");
                for (int j = 0; j < splits.length; j++) {
                    splits[j] = splits[j].trim();
                }

                System.out.print(count + ". ");
                for (String s : splits) {
                    System.out.println(s);
                    Thread.sleep(1000);
                }
                System.out.println("\n\n");

                count += 1;
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.exit(0);
    });

    public FastRun(List<String> words, int num) {
        this.words = words;
        this.num = num;
        count = 1;
    }

    public void run() {
        fastReadThread.start();
    }
}