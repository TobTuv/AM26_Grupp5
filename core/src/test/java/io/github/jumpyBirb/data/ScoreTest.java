package io.github.jumpyBirb.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoreTest {

    @Test
    void update_shouldNotChangeVisualScore_whenNotRunning() {
        Score score = new Score();

        long initialVisual = score.getVisualScore();

        score.update(1.0f, false);

        assertEquals(initialVisual, score.getVisualScore());
    }

    @Test
    void update_shouldIncreaseScore_whenRunning() {
        Score score = new Score();

        long before = (long) score.getVisualScore();

        score.update(1.0f, true);

        long after = (long) score.getVisualScore();

        assertTrue(after >= before);
    }

    @Test
    void score_shouldIncreaseAfterMultipleUpdates() {
        Score score = new Score();

        long before = (long) score.getVisualScore();

        score.update(0.1f, true);
        score.update(0.1f, true);
        score.update(0.1f, true);

        long after = (long) score.getVisualScore();

        assertTrue(after > before);
    }

    @Test
    void score_shouldStayConstant_whenPausedOverTime() {
        Score score = new Score();

        long before = (long) score.getVisualScore();

        score.update(0.1f, false);
        score.update(0.2f, false);
        score.update(0.3f, false);

        long after = (long) score.getVisualScore();

        assertEquals(before, after);
    }

    @Test
    void reset_shouldClearAllState() {
        Score score = new Score();

        score.update(0.1f, true);

        long beforeReset = (long) score.getVisualScore();

        score.reset();

        long afterReset = (long) score.getVisualScore();

        assertTrue(beforeReset > 0);
        assertEquals(0, afterReset);
    }


}


