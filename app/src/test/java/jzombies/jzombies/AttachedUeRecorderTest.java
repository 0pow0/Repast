package jzombies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class AttachedUeRecorderTest {
  @Test
  public void testInc() {
    AttachedUeRecorder recorder = new AttachedUeRecorder();
    recorder.inc(0, 0);
    assertEquals(recorder.getByTimestepAndEnbId(0, 0), 1);
    recorder.inc(0, 1);
    assertEquals(recorder.getByTimestepAndEnbId(0, 1), 1);
    recorder.inc(0, 0);
    assertEquals(recorder.getByTimestepAndEnbId(0, 0), 2);
    assertEquals(recorder.getByTimestepAndEnbId(1, 0), 0);
  }
}
