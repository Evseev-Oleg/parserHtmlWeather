import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * класс парсит html страницу с погодой
 */
public class Parser {
    private static final Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    /**
     * метод выводит на экран строки с данными погоды
     *
     * @param values все значения для столбца
     * @param index  число с которого нужно начинать цикл
     * @return результат количества выведенных строк
     */
    private static int printForValues(Elements values, int index) {
        int i = 0;
        if (index == 0) {
            switch (values.size()) {
                case 17 -> { //если на дате сегодня осталась только одна строка ночь
                    for (i = index; i < 1; i++) {
                        Element valueLine = values.get(i); //получили строку из множества строк Elements по индексу
                        for (Element td : valueLine.select("td")) { //все елементы с td
                            System.out.print(td.text() + "\t\t");
                        }
                        System.out.println();
                    }
                    return i;
                }
                case 18 -> { //если на дате  сегодня остались две строчки ночь и вечер
                    for (i = index; i < 2; i++) {
                        Element valueLine = values.get(i);
                        for (Element td : valueLine.select("td")) {
                            System.out.print(td.text() + "\t\t");
                        }
                        System.out.println();
                    }
                    return i;
                }
                case 19 -> { //если на дате "сегодня" остались три строчки ночь вечер и день
                    for (i = index; i < 3; i++) {
                        Element valueLine = values.get(i);
                        for (Element td : valueLine.select("td")) {
                            System.out.print(td.text() + "\t\t");
                        }
                        System.out.println();
                    }
                    return i;
                }
                case 20 -> { //если на дате "сегодня" все строчки утро день вечер ночь
                    for (i = index; i < 4; i++) {
                        Element valueLine = values.get(i);
                        for (Element td : valueLine.select("td")) {
                            System.out.print(td.text() + "\t\t");
                        }
                        System.out.println();
                    }
                    return i;
                }
            }
        } else { // для 4 дней далее
            for (i = index; i < index + 4; i++) {
                Element valueLine = values.get(i);
                for (Element td : valueLine.select("td")) {
                    System.out.print(td.text() + "\t\t");
                }
                System.out.println();
            }
        }
        return i;
    }

    /**
     * @param date принимает строку от парсера
     * @return возвращает дату обработанную регулярным
     * выражением
     */
    private static String getDate(String date) throws Exception {
        Matcher matcher = pattern.matcher(date);
        if (matcher.find()) { //если matcher что то нашел(.find())
            return matcher.group(); //сгруппируй и верни
        }
        throw new Exception("Can't extract date from string");// или выкидываем исключение
    }

    /**
     * @return возвращает страницу html кода
     * @throws IOException .
     */
    private static Document getDoc() throws IOException {
        String url = "http://pogoda.spb.ru/";
        return Jsoup.parse(new URL(url), 3000); //3000 миллисекунд это столько метод будет ждать ответа от страницы
    }

    public static void main(String[] args) throws Exception {
        Document doc = getDoc();
        Element tableWeather = doc.select("table[class=wt]").first(); //первый елемент с классом wt(table сданными о погоде)
        Elements names = tableWeather.select("tr[class=wth]"); //запрос всех элементов с классом wth(название столбцов)
        Elements values = tableWeather.select("tr[valign=top]");//запрос данных где valign = top(все значения для столбцов)
        int index = 0;
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text();
            String date = getDate(dateString);
            System.out.println(date + "\t\t" + "Явления" + "\t\t" + "\t\t"
                    + "Темп" + "\t\t" + "Давл" + "\t\t" + "Влажн" + "\t\t" + "Ветер");
            index = printForValues(values, index);
        }
    }
}
