package com.talentboozt.s_backend.domains.edu.seo.og;

import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 * Dynamic Open Graph (OG) Image Generation Service.
 * Leverages native JDK Graphics2D to draw high-definition, premium branded social share cards
 * dynamically. Eliminates heavy system dependencies like node screenshotters.
 */
@Service
public class OgImageService {

    /**
     * Compiles and outputs a PNG byte array for an Open Graph social sharing card.
     */
    public byte[] generateCourseOgImage(String title, String instructor, String medium) {
        try {
            int width = 1200;
            int height = 630;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            // Set rendering parameter guidelines for ultra-sharp typography
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // 1. Draw premium dark luxury gradient backdrop
            GradientPaint backgroundGradient = new GradientPaint(
                0, 0, new Color(12, 12, 16), 
                width, height, new Color(28, 20, 48)
            );
            g.setPaint(backgroundGradient);
            g.fillRect(0, 0, width, height);

            // 2. Draw border frame with Talnova Orange highlight
            g.setColor(new Color(255, 107, 0, 200));
            g.setStroke(new BasicStroke(16));
            g.drawRect(8, 8, width - 16, height - 16);

            // 3. Draw Branding Header
            g.setColor(new Color(255, 107, 0));
            g.setFont(new Font("SansSerif", Font.BOLD, 36));
            g.drawString("TALNOVA EDU", 80, 100);

            // 4. Draw Language tag
            g.setColor(new Color(200, 200, 200));
            g.setFont(new Font("SansSerif", Font.ITALIC, 24));
            g.drawString("Official Curriculum Medium: " + (medium != null ? medium : "Sinhala"), 80, 150);

            // 5. Draw dynamic Course Title with clean truncation safety
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 54));
            String displayTitle = (title != null) ? title : "Advanced Level Revision Core";
            if (displayTitle.length() > 36) {
                g.drawString(displayTitle.substring(0, 36) + "...", 80, 280);
            } else {
                g.drawString(displayTitle, 80, 280);
            }

            // 6. Draw Instructor Profile Label
            g.setColor(new Color(230, 230, 230));
            g.setFont(new Font("SansSerif", Font.BOLD, 32));
            g.drawString("Conducted by: " + (instructor != null ? instructor : "Verified Educator"), 80, 420);

            // 7. Draw dynamic educational footer
            g.setColor(new Color(150, 150, 150));
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g.drawString("Accredited Online Revision Guides | Sri Lanka High-Performance LMS", 80, 520);

            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
