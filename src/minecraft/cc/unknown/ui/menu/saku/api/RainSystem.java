package cc.unknown.ui.menu.saku.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class RainSystem {
    private List<Particle> particles;
    private static int screenWidth;
	private static int screenHeight;
    private static final Random random = new Random();

    public RainSystem(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        particles = new ArrayList<>();
    }

    public void update() {
        if (particles.size() < 200) {
            particles.add(new Particle(screenWidth, screenHeight));
        }

        for (Particle particle : particles) {
            particle.update();
        }
        
        particles.removeIf(particle -> particle.isOutOfBounds());
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBegin(GL11.GL_POINTS);
        for (Particle particle : particles) {
            particle.render();
        }
        
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public class Particle {
        private float x, y, speedX, speedY;
        private int size;

        public Particle(int screenWidth, int screenHeight) {
            this.x = random.nextFloat() * screenWidth;
            this.y = 0;
            this.speedX = (random.nextFloat() - 0.5f) * 0.2f;
            this.speedY = (random.nextFloat() * 0.5f) + 0.2f;

            this.size = 1;
        }

        public void update() {
            this.x += speedX;
            this.y += speedY;
            
            if (y > screenHeight) {
                this.y = 0;
                this.x = random.nextFloat() * screenWidth;
            }
        }

        public void render() {
        	GL11.glColor4f(1f, 1f, 1f, 1f);
        	GL11.glVertex2f(x, y);
        }

        public boolean isOutOfBounds() {
            return y > screenHeight;
        }
    }
}
