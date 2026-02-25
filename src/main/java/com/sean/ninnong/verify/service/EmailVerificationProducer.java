package com.sean.ninnong.verify.service;

import com.sean.ninnong.verify.dto.EmailVerificationMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationProducer {

    private final SqsTemplate sqsTemplate;

    @Value("${cloud.aws.sqs.queue.email-verification}")
    private String queueUrl;

    public void send(String email, String code) {
        sqsTemplate.send(queueUrl, new EmailVerificationMessage(email, code));
    }
}
