/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import objectiveethics.values.logic.method.information.GZIPInfo;
import objectiveethics.values.logic.method.information.MarkovInfo;
import objectiveethics.values.logic.method.information.MaxInfo;

/**
 *
 * @author zsolt
 */
public class Comparison {
    
    private static DecimalFormat format = new DecimalFormat("0");

    public static void main(String[] args) {

        StringBuilder fibonacci = new StringBuilder();
        fibonacci.append("0");
        BigDecimal n1 = BigDecimal.ZERO;
        BigDecimal n2 = BigDecimal.ONE;
        while (fibonacci.length() < 1000) {
            BigDecimal n3 = n1.add(n2);
            fibonacci.append(n3);
            n1 = n2;
            n2 = n3;
        }
        String s = fibonacci.toString().substring(0,1000);
        test(s, "Fibonacci (" + s.length(), "a,b,f=0,1,\"0\"\n"
                + "while len(f)<1000:a,b,f=b,a+b,f+str(b)");

        s = """
            John Muir (/mj\u028a\u0259r/ MURE; April 21, 1838\u00a0\u2013 December 24, 1914),[1] also known as "John of the Mountains" and "Father of the National Parks",[2] was a Scottish-born American[3][4]:\u200a42\u200a naturalist, author, environmental philosopher, botanist, zoologist, glaciologist, and early advocate for the preservation of wilderness in the United States. 
            His books, letters and essays describing his adventures in nature, especially in the Sierra Nevada, have been read by millions. His activism helped to preserve the Yosemite Valley and Sequoia National Park, and his example has served as an inspiration for the preservation of many other wilderness areas. The Sierra Club, which he co-founded, is a prominent American conservation organization. In his later life, Muir devoted most of his time to his wife and the preservation of the Western forests. As part of the campaign to make Yosemite a national park, Muir published two landmark articles on wilderness preservation in The Century Magazine, "The Treasure""";

        test(s, "English text (" + s.length() + ")", s);
        s = createText();

        test(s, "Generated text (" + s.length() + ")", "import random as r;c='ETAOINSRHDLUCMFYWGPBVKXQJZ ';f=[12,9,8,7.5,7,6.7,6.3,6,5.9,4.3,4,2.8,2.7,2.6,2.3,2.1,2,2,1.8,1.5,1.1,0.7,0.2,0.1,0.1,0.07,5];s=sum(f);m=[sum(f[:i+1])for i in range(len(f))];def g():z=r.random()*s;ch=next(c[j]for j in range(len(m))if z<m[j]);return ch if ch==' 'else(ch.lower()if r.random()<0.9 else ch.upper());print(''.join(g()for _ in range(100)))");
        
        s = binary();

        test(s, "Binary text (" + s.length() + ")", s);
        
        s = circles();

        test(s, "Circle text (" + s.length() + ")", "for$y(0..24){for$x(0..39){print sqrt(($x-20)**2+($y-12.5)**2)%3<1?1:0}print\"\\n\"}");
    }

    private static void test(String s, String name, String code) {
        System.out.println();
        System.out.println(s);
        System.out.println();
        System.out.println(code);
        System.out.println();
        
        MaxInfo maxInfo = new MaxInfo();
        double max = (maxInfo.value(s)+maxInfo.value(s)+maxInfo.value(s)+maxInfo.value(s))/4;

        MarkovInfo markovInfo = new MarkovInfo();
        double markov = (markovInfo.value(s)+markovInfo.value(s)+markovInfo.value(s)+markovInfo.value(s))/4;

        GZIPInfo gzipInfo = new GZIPInfo();
        double gzip = (gzipInfo.value(s)+gzipInfo.value(s)+gzipInfo.value(s)+gzipInfo.value(s)+gzipInfo.value(s)+
                gzipInfo.value(s)+gzipInfo.value(s)+gzipInfo.value(s)+gzipInfo.value(s)+gzipInfo.value(s))/10;

        double kolmogorov = (gzipInfo.value(code)+gzipInfo.value(code)+gzipInfo.value(code)+gzipInfo.value(code)
                +gzipInfo.value(code)+gzipInfo.value(code)+gzipInfo.value(code)+gzipInfo.value(code)+gzipInfo.value(code)+gzipInfo.value(code))/10;

        System.out.println();
        System.out.println(" --------- " + name + " --------- ");
        System.out.println(format.format(max));
        System.out.println(format.format(markov));
        System.out.println(format.format(gzip));
        System.out.println(format.format(kolmogorov));
    }

    private static String createText() {
        // Angol nyelv leggyakoribb karakterei és közelítő gyakoriságuk
        // (Leegyszerűsített frekvenciák, kb. százalékban kifejezve)
        char[] chars = {
            'E', 'T', 'A', 'O', 'I', 'N', 'S', 'R', 'H', 'D', 'L', 'U', 'C', 'M',
            'F', 'Y', 'W', 'G', 'P', 'B', 'V', 'K', 'X', 'Q', 'J', 'Z', ' ' // köztes szóköz is
        };

        double[] freqs = {
            12.0, 9.0, 8.0, 7.5, 7.0, 6.7, 6.3, 6.0, 5.9, 4.3, 4.0, 2.8,
            2.7, 2.6, 2.3, 2.1, 2.0, 2.0, 1.8, 1.5, 1.1, 0.7, 0.2, 0.1,
            0.1, 0.07, 5.0 // a szóköz kapjon egy mérsékelt esélyt (5%)
        };

        // A frekvenciák halmozott összege a későbbi kiválasztáshoz
        double[] cumulative = new double[freqs.length];
        double sum = 0.0;
        for (int i = 0; i < freqs.length; i++) {
            sum += freqs[i];
            cumulative[i] = sum;
        }

        StringBuilder sb = new StringBuilder();
        Random rand = new Random();

        // 100 karakter előállítása
        for (int i = 0; i < 1000; i++) {
            double r = rand.nextDouble() * sum; // Véletlen érték [0, sum) között
            // Kiválasztjuk a karaktert a halmozott frekvenciák alapján
            for (int j = 0; j < cumulative.length; j++) {
                if (r < cumulative[j]) {
                    sb.append(Math.random() < 0.01 ? chars[j] : Character.toLowerCase(chars[j]));
                    break;
                }
            }
        }


        return sb.toString();
    }
    
    private static String binary() {
        final int LENGTH = 1000;
        char[] sequence = new char[LENGTH];
        
        // 1. Véletlenszám-generátor példány
        Random rand = new Random();
        
        // 2. Előírunk egy 50–50% arányt a két karakter számára
        //    (Ez nem tökéletes véletlen, de biztosítja a kívánt arányt.)
        int half = LENGTH / 8;
        int countA = half;
        
        // Feltöltjük a tömböt: félig A-k, félig B-k
        // Majd később véletlenszerűen megkeverjük.
        for (int i = 0; i < countA; i++) {
            sequence[i] = '0';
        }
        for (int i = countA; i < LENGTH; i++) {
            sequence[i] = '1';
        }
        
        // 3. Véletlenszerű keverés (Fisher-Yates shuffle / Durstenfeld)
        for (int i = LENGTH - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            char temp = sequence[i];
            sequence[i] = sequence[j];
            sequence[j] = temp;
        }
        
        // 4. Beágyazunk néhány fix mintát (ezek a "tervezett" elemek).
        //    Például egy 7 karakteres minta: "AABBAAB"
        //    Ezt néhányszor véletlenszerű pozícióba illesztjük be.
        String pattern = "00000";
        int patternLength = pattern.length();
        
        // Tegyük be 3 különböző helyre, amennyiben belefér.
        for (int k = 0; k < 20; k++) {
            int startPos = rand.nextInt(LENGTH - patternLength);
            for (int i = 0; i < patternLength; i++) {
                sequence[startPos + i] = pattern.charAt(i);
            }
        }
        
        // 5. Hozzunk létre egy hosszabb redundant szakaszt is, pl. 20 ugyanolyan karakter egymás után.
        //    Ezt is véletlen pozícióba illesztjük be, persze csak akkor, ha belefér a tömbbe.
        int redundantSegmentLength = 200;
        char redundantChar =  '1'; // véletlenszerűen A vagy B
        
        int redundantStartPos = rand.nextInt(LENGTH - redundantSegmentLength);
        for (int i = 0; i < redundantSegmentLength; i++) {
            sequence[redundantStartPos + i] = redundantChar;
        }
        
        // 6. Az eredmény (char tömb) kiírása
        String result = new String(sequence);

        
        return result;
    }



    public static String circles() {
        int width = 40;       // Mátrix szélessége
        int height = 25;      // Mátrix magassága
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        double ringSpacing = 3.0;  // A körgyűrűk közötti távolság
        
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                double dx = x - centerX;
                double dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Ha a távolság mod ringSpacing egy bizonyos kis érték alá esik, akkor tegyünk "1"-et
                // Például ringSpacing = 3.0 esetén 0 <= distance % 3 < 1 => "1"
                if (distance % ringSpacing < 1.0) {
                    row.append('1');
                } else {
                    row.append('0');
                }
            }
            System.out.println(row.toString());
            sb.append(row.toString());
        }
        return sb.toString();
    }
}
