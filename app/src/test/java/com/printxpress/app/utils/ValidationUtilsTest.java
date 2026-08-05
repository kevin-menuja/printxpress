package com.printxpress.app.utils;
import org.junit.Test;import static org.junit.Assert.*;
public class ValidationUtilsTest {
    @Test public void acceptsValidSriLankanMobile(){assertTrue(ValidationUtils.validSriLankanPhone("0771234567"));}
    @Test public void rejectsInvalidPhone(){assertFalse(ValidationUtils.validSriLankanPhone("771234567"));}
    @Test public void acceptsValidVisaLuhnNumber(){assertTrue(ValidationUtils.passesLuhn("4111111111111111"));}
    @Test public void rejectsInvalidCardNumber(){assertFalse(ValidationUtils.passesLuhn("4111111111111112"));}
    @Test public void acceptsFutureExpiry(){assertTrue(ValidationUtils.validExpiry("12/30",7,2026));}
    @Test public void rejectsPastExpiry(){assertFalse(ValidationUtils.validExpiry("06/26",7,2026));}
    @Test public void rejectsInvalidExpiryMonth(){assertFalse(ValidationUtils.validExpiry("13/30",7,2026));}
    @Test public void calculatesOrderTotal(){assertEquals(3600,ValidationUtils.orderTotal(1200,3),0.01);}
}
