/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import objectiveethics.values.logic.method.information.GZIPInfo;
import objectiveethics.values.logic.method.information.RLEShannonInfo;
import objectiveethics.values.logic.method.information.SSMInfo;
import objectiveethics.values.logic.method.entropy.ShannonEntropy;

public class MarkovChain {

    // Állapotok (karakterek) és azok átmeneti valószínűségei
    private Map<Character, Map<Character, Double>> transitionMatrix;
    private Random random;

    public MarkovChain() {
        transitionMatrix = new HashMap<>();
        random = new Random();
        initializeTransitionMatrix();
    }

    // Inicializáljuk az átmeneti mátrixot
    private void initializeTransitionMatrix() {
        // Például 4 állapot: A, B, C, D - nem periodikus
        transitionMatrix.put('A', new HashMap<>());
        transitionMatrix.get('A').put('A', 0.1);
        transitionMatrix.get('A').put('B', 0.4);
        transitionMatrix.get('A').put('C', 0.2);
        transitionMatrix.get('A').put('D', 0.3);

        transitionMatrix.put('B', new HashMap<>());
        transitionMatrix.get('B').put('A', 0.003);
        transitionMatrix.get('B').put('B', 0.99);
        transitionMatrix.get('B').put('C', 0.003);
        transitionMatrix.get('B').put('D', 0.004);

        transitionMatrix.put('C', new HashMap<>());
        transitionMatrix.get('C').put('A', 0.3);
        transitionMatrix.get('C').put('B', 0.3);
        transitionMatrix.get('C').put('C', 0.1);
        transitionMatrix.get('C').put('D', 0.3);

        transitionMatrix.put('D', new HashMap<>());
        transitionMatrix.get('D').put('A', 0.4);
        transitionMatrix.get('D').put('B', 0.2);
        transitionMatrix.get('D').put('C', 0.3);
        transitionMatrix.get('D').put('D', 0.1);
    }

    // Egy következő állapot kiválasztása a jelenlegi állapotból az átmeneti valószínűségek alapján
    private char getNextState(char currentState) {
        Map<Character, Double> probabilities = transitionMatrix.get(currentState);
        double p = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (Map.Entry<Character, Double> entry : probabilities.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (p <= cumulativeProbability) {
                return entry.getKey();
            }
        }

        // Biztonsági visszatérés, ha valami hiba lenne az átmeneti mátrixban
        return currentState;
    }

    // Karakterlánc generálása a Markov-lánc alapján
    public String generateString(int length, char initialState) {
        StringBuilder result = new StringBuilder();
        char currentState = initialState;

        for (int i = 0; i < length; i++) {
            result.append(currentState);
            currentState = getNextState(currentState);
        }

        return result.toString();
    }

    public static void main(String[] args) {
//        MarkovChain markovChain = new MarkovChain();
//        int length = 1000;  // Generálandó karakterlánc hossza
//        char initialState = 'A';  // Kezdeti állapot
//
//        String generatedString = markovChain.generateString(length, initialState);
//        System.out.println("Generated String: " + generatedString);
        
        String s = "A matematikában a Markov-lánc egy olyan diszkrét sztochasztikus folyamatot jelent, amely Markov-tulajdonságú. Nevét egy orosz matematikusról, Andrej Markovról kapta, aki hírnevét a tudomány ezen ágában végzett kutatásaival szerezte. Markov-tulajdonságúnak lenni röviden annyit jelent, hogy adott jelenbeli állapot mellett, a rendszer jövőbeni állapota nem függ a múltbeliektől. Másképpen megfogalmazva ez azt is jelenti, hogy a jelen leírása teljesen magába foglalja az összes olyan információt, ami befolyásolhatja a folyamat jövőbeli helyzetét. Vegyünk például egy olyan fizikai rendszert, amelynek lehetséges állapotai A 0 , A 1 , … , A k , … {\\displaystyle A_{0},A_{1},\\dots ,A_{k},\\dots }. Az S rendszer az idő múlásával állapotait véletlenszerűen változtatja; vizsgáljuk a rendszer állapotait a t = 0 , 1 , … {\\displaystyle t=0,1,\\dots } diszkrét időpontokban, és X n {\\displaystyle X_{n}} legyen egyenlő k-val, ha S az n időpontban az A k {\\displaystyle A_{k}} állapotban van. Ezzel a terminológiával a Markov-tulajdonság így is megfogalmazható: A rendszer korábbi állapotai a későbbi állapotokra csak a jelen állapoton keresztül gyakorolhatnak befolyást.\n" +
"\n" +
"Adott jelen mellett tehát a jövő feltételesen független a múlttól. Semmi, ami a múltban történt, nem hat, nem ad előrejelzést a jövőre nézve, a jövőben minden lehetséges. Alapvető példa erre az érmedobás – ha fejet dobunk elsőre, másodikra ugyanúgy 50/50%-kal dobhatunk írást vagy fejet egyaránt. Ha pedig 100-szor dobunk fejet egymás után, akkor is ugyanannyi a valószínűsége, hogy fejet kapunk 101.-re, mint annak, hogy írást, az előzőekhez hasonlóan-a múlt tehát nem jelzi előre a jövőbeli eredményt. A jelen állapot az, hogy van egy érménk (nem cinkelt), fejjel és írással a két oldalán. Szabályos kereteket feltételezve semmi más nem befolyásolhatja a jövőbeni dobás alakulását.\n" +
"\n" +
"Minden egyes pillanatban a rendszer az adott valószínűségi változó eloszlása alapján vagy megváltoztatja az állapotát a jelenbeli állapotától, vagy ugyanúgy marad. Az állapotváltozásokat átmenetnek nevezzük, és azokat a valószínűségeket, melyek a különböző állapotváltozásokra vonatkoznak, átmenet-valószínűségeknek nevezzük. Ez a fogalom megtalálható a véletlen analízisben is.\n" +
"Formális definíció\n" +
"\n" +
"A nem független valószínűségi változók egy jelentős osztálya a Markov-láncok. Egy X1, X2, X3, … valószínűségi változó sorozatot Markov-láncnak nevezzük, ha az alábbi feltétel teljesül rá minden n természetes számra és minden x, x1, x2,…, xn valós számrendszerre 1 valószínűséggel:\n" +
"\n" +
"    Pr ( X n + 1 = x | X n = x n , … , X 1 = x 1 ) = Pr ( X n + 1 = x | X n = x n ) . {\\displaystyle \\Pr(X_{n+1}=x|X_{n}=x_{n},\\ldots ,X_{1}=x_{1})=\\Pr(X_{n+1}=x|X_{n}=x_{n}).\\,}\n" +
"\n" +
"Ezt a feltételt szokás Markov-tulajdonságnak is nevezni. Jelen esetben az Xi lehetséges értékei egy megszámlálható S halmazból valóak. Ezt az S halmazt állapothalmaznak nevezzük. A Markov-láncokat ábrázolhatjuk irányított gráfokkal is, ahol a csúcsok az egyes állapotok, a két csúcsot összekötő élre írt érték (felfogható súlyokként is) pedig az egyik állapotból a másikba kerülés valószínűsége (iránynak megfelelően).\n" +
"Típusai\n" +
"\n" +
"Stacionárius átmenetvalószínűségű (homogén) Markov-láncról beszélünk, ha az átmenet-valószínűségek nem függnek az időtől, azaz:\n" +
"\n" +
"    Pr ( X n + 1 = j ∣ X n = i ) = p i j {\\displaystyle \\Pr(X_{n+1}=j\\mid X_{n}=i)=p_{ij}\\,}\n" +
"\n" +
"n-től függetlenül.\n" +
"\n" +
"m-edrendű Markov-láncok az olyan Markov-láncok, melyekre (véges m esetén):\n" +
"\n" +
"    Pr ( X n = x n | X n − 1 = x n − 1 , X n − 2 = x n − 2 , … , X 1 = x 1 ) {\\displaystyle \\Pr(X_{n}=x_{n}|X_{n-1}=x_{n-1},X_{n-2}=x_{n-2},\\dots ,X_{1}=x_{1})}\n" +
"    = Pr ( X n = x n | X n − 1 = x n − 1 , X n − 2 = x n − 2 , … , X n − m = x n − m ) {\\displaystyle =\\Pr(X_{n}=x_{n}|X_{n-1}=x_{n-1},X_{n-2}=x_{n-2},\\dots ,X_{n-m}=x_{n-m})}\n" +
"\n" +
"minden n-re, vagyis a rendszer csak m időszakra emlékszik vissza, a korábbiak állapotok nem befolyásolják a jövőt.\n" +
"\n" +
"m=1 esetén egyszerű Markov-láncnak nevezzük.\n" +
"Példa\n" +
"\n" +
"    Legyen egy dolog két állapota A és E, azaz az S állapothalmaz kételemű, S = {A, E}.\n" +
"\n" +
"	\n" +
"\n" +
"    Ha a dolog egy t időpontban A állapotban van, akkor annak a valószínűsége, hogy (t+1)-ben E állapotba kerül, legyen 0,4. Annak a valószínűsége, hogy továbbra is A-ban marad, 0,6 lesz (mivel a két feltételes valószínűség összege szükségszerűen 1 kell legyen).\n" +
"    Ha a dolog ugyanebben a t időpontban E állapotban van, akkor annak a valószínűsége, hogy (t+1)-ben A állapotba kerül, legyen 0,7. Annak a valószínűsége, hogy továbbra is E-ben marad, 0,3 lesz.\n" +
"\n" +
"Ezt ábrázolja a bal oldali irányított gráf, ahol A és E állapotokat egy-egy kör szemlélteti, az egyes körökből kimenő nyilak mellé írt számok pedig az átmenet-valószínűségek.\n" +
"\n" +
"Gyakran csak az állapotváltozások nyilait rajzolják fel az ilyen ábrákon, mivel annak az valószínűsége, hogy egy állapot változatlan marad, kiszámolható úgy, hogy 1-ből levonjuk az állapotot szemléltető körből kimenő nyilak fölé írt valószínűségek összegét.\n" +
"\n" +
"    Ez egy stacionárius, egyszerű (elsőrendű) Markov-lánc.\n" +
"\n" +
"    Valamely véges állapotú gép jól reprezentálhatja a Markov-láncokat. Feltételezzük, hogy a masinánk lehetséges inputjai egymástól függetlenek és egyenletes eloszlásúak. Ekkor, ha a gépezet egy tetszőleges y állapotban van az n-edik időpillanatban, akkor annak valószínűsége, hogy az n + 1-edik pillanatban az x állapotban lesz, csak a jelenlegi állapottól függ.";

        ShannonEntropy sh = new ShannonEntropy();
        GZIPInfo gi = new GZIPInfo();
        RLEShannonInfo rsi = new RLEShannonInfo();
        SSMInfo ssmi = new SSMInfo();
        
        System.out.println("Shannon entropy: " + sh.value(s));
        System.out.println("GZIP entropy: " + gi.value(s)/s.length());
        System.out.println("RLE-Shannon entropy: " + rsi.value(s)/s.length());
        System.out.println("SSM entropy: " + ssmi.value(s)/s.length());
    }
}
