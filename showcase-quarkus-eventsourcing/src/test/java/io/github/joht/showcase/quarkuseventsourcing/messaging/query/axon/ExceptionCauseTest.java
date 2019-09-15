package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.ExceptionCause;

class ExceptionCauseTest {

    private Throwable exceptionWithoutCause = new IllegalArgumentException("TestException");
    private Throwable exceptionWithCause = new IllegalStateException("TestExceptionWithCause", exceptionWithoutCause);

    ExceptionCause exceptionCauseToTest;

    @Test
    @DisplayName("without a cause a new IllegalStateException should be thrown using the original message")
    void testShouldUnwrapIllegalStateExceptionWithOriginalMessageWithoutCause() {
        exceptionCauseToTest = new ExceptionCause(exceptionWithoutCause);
        RuntimeException unwrapped = exceptionCauseToTest.unwrapped();
        assertEquals(exceptionWithoutCause.getMessage(), unwrapped.getMessage());
        assertEquals(IllegalStateException.class, unwrapped.getClass());
    }

    @Test
    @DisplayName("an existing exception cause should be unwraped")
    void testShouldUnwrapExistingCause() {
        exceptionCauseToTest = new ExceptionCause(exceptionWithCause);
        RuntimeException unwrapped = exceptionCauseToTest.unwrapped();
        assertEquals(exceptionWithoutCause, unwrapped);
    }

    @Test
    @DisplayName("an existing checked exception is unwrapped as cause inside an IllegalStateException")
    void testShouldUnwrapIllegalStateExceptionWithCheckedExceptionCause() {
        Throwable checkedExceptionCause = new Exception("TestCheckedException");
        Throwable exceptionWithCheckedExceptionCause = new IllegalStateException("CheckedExceptionCause", checkedExceptionCause);

        exceptionCauseToTest = new ExceptionCause(exceptionWithCheckedExceptionCause);

        RuntimeException unwrapped = exceptionCauseToTest.unwrapped();
        assertEquals(IllegalStateException.class, unwrapped.getClass());
        assertEquals(checkedExceptionCause, unwrapped.getCause());
    }

}
