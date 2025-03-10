package com.project.pay_service.ports.input.message.listener;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.constants.PaymentStatus;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.pay_service.exception.PaymentDomainException;
import com.project.pay_service.exception.PaymentNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestKafaListener {

    private final PaymentRequestMessageListener paymentRequestMessageListener;

    @KafkaListener(id="payment-request", topics = "order")
    public void receive(List<PaymentRequest> messages) {


        messages.forEach(paymentRequest -> {
        try {
                if (PaymentStatus.COMPLETED == paymentRequest.getPaymentStatus()) {
                    log.info("Processing payment for order id: {}", paymentRequest.getOrderId());
                    paymentRequestMessageListener.completePayment(paymentRequest);
                } else if(PaymentStatus.CANCELED == paymentRequest.getPaymentStatus()) {
                    log.info("Cancelling payment for order id: {}", paymentRequest.getOrderId());
                    paymentRequestMessageListener.cancelPayment(paymentRequest);
                }
            } catch (DataAccessException e) {
                SQLException sqlException = (SQLException) e.getRootCause();
                if (sqlException != null && sqlException.getSQLState() != null &&
                        "23000" == sqlException.getSQLState()) {
                    //NO-OP for unique constraint exception
                    log.error("Caught unique constraint exception with sql state: {} " +
                                    "in PaymentRequestKafkaListener for order id: {}",
                            sqlException.getSQLState(), paymentRequest.getOrderId());
                } else {
                    throw new PaymentDomainException("Throwing DataAccessException in" +
                            " PaymentRequestKafkaListener: " + e.getMessage(), e);
                }
            } catch (PaymentNotFoundException e) {
                //NO-OP for PaymentNotFoundException
                log.error("No payment found for order id: {}", paymentRequest.getOrderId());
            }
        });

    }
}
