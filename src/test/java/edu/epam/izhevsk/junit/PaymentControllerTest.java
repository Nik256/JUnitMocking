package edu.epam.izhevsk.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class PaymentControllerTest {
    @Mock
    AccountService accountService;

    @Mock
    DepositService depositService;

    @InjectMocks
    PaymentController paymentController;

    @Before
    public void initMocks() throws InsufficientFundsException {
        doReturn(true).when(accountService).isUserAuthenticated(100L);
        doReturn(false).when(accountService).isUserAuthenticated(not(eq(100L)));
        doReturn("").when(depositService).deposit(lt(100L), anyLong());
        doThrow(InsufficientFundsException.class).when(depositService).deposit(geq(100L), anyLong());
    }

    @Test
    public void testSuccessfulDeposition() throws InsufficientFundsException {
        paymentController.deposit(50L, 100L);
        verify(accountService, times(1)).isUserAuthenticated(100L);
    }

    @Test(expected = SecurityException.class)
    public void depositionForUnauthorizedUserShouldThrowSecurityException() throws InsufficientFundsException {
        paymentController.deposit(anyLong(), 5L);
    }

    @Test(expected = InsufficientFundsException.class)
    public void depositionOfLargeAmountShouldThrowInsufficientFundsException() throws InsufficientFundsException {
        paymentController.deposit(500L, 100L);
    }
}