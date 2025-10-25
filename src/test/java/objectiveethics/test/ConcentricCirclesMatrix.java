/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

/**
 *
 * @author zsolt
 */
public class ConcentricCirclesMatrix {
    public static void main(String[] args) {
        int width = 40;       // Mátrix szélessége
        int height = 25;      // Mátrix magassága
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        double ringSpacing = 3.0;  // A körgyűrűk közötti távolság

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
            // Kinyomtatjuk az adott sort
            System.out.println(row.toString());
        }
    }
}
