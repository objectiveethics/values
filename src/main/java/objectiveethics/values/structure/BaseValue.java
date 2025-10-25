/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.structure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotáció a Value interfészt megvalósító osztályok metaadatainak rögzítésére.
 * Lehetővé teszi a név és leírás megadását az osztály szintjén.
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseValue {

    /**
     * Az érték számítási módszer egyedi azonosítója.
     * Pozitív egész szám, ami egyedileg azonosítja az algoritmus.
     * 
     * @return az algoritmus egyedi azonosítója
     */
    long id() default 0;

    /**
     * Az érték számítási módszer kategóriája.
     * Például: "information", "entropy", "complexity" stb.
     * 
     * @return a kategória neve
     */
    String category() default "";

    /**
     * Az érték számítási módszer mozaikszava (akroníma).
     * Az algoritmus nevének logikus rövidítése, például:
     * ShannonInfo -> "ISH", RLEInfo -> "IRLE", MaxInfo -> "IMAX", SCMInfo -> "ISCM"
     * 
     * @return a módszer mozaikszava
     */
    String acronym() default "";

    /**
     * Az érték számítási módszer neve.
     * Ha nincs megadva, akkor a name() metódus eredményét használja.
     * 
     * @return a módszer neve
     */
    String name() default "";
    
    /**
     * Az érték számítási módszer részletes leírása.
     * 
     * @return a módszer leírása
     */
    String description() default "";

    /**
     * Minimum recommended sample size for estimation.
     * Default: 10000
     * @return minimum estimation size
     */
    int minEstimationSize() default 10000;

    /**
     * The algorithm pseudocode as a textual enumeration.
     * Summary of the main steps of the algorithm in simple, understandable form.
     * 
     * @return the algorithm pseudocode
     */
    String algorithm() default "";

    /**
     * URL hivatkozás a módszerrel kapcsolatos cikkre vagy dokumentációra.
     * Opcionális mező, amely tartalmazhat egy URL-t, ahol további információk
     * találhatók az algoritmusról vagy a módszerről.
     * 
     * @return a cikkre mutató URL
     */
    String article() default "";

}
