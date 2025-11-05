package com.sistema_seguimiento.services;

import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationServiceTest {
    @Test
    public void given_UserName_when_generateReminderMessage_then_MessageIsCorrect(){
        System.out.println("--- Ejecutando Test 7/12 (NotificatioService Logic)-----");

        NotificationService service = new NotificationService();
        String userName = "Jhair";
        String expectedMessage = "!Hola Jhair, no olvides registrar tu ánimo de hoy!";

        String actualMessage = service.generateReminderMessage(userName);

        assertEquals(expectedMessage, actualMessage);
    }
}