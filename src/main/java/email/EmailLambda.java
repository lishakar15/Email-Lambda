package email;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmailLambda implements RequestHandler<SQSEvent, Void> {
    private AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.defaultClient();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                JSONObject jsonObject = new JSONObject(message.getBody());
                JSONArray emails = jsonObject.getJSONArray("emails");
                String subject = jsonObject.getString("subject");
                String body = jsonObject.getString("body");
                for (int i = 0; i < emails.length(); i++) {
                    sendEmail(emails.getString(i), subject, body);
                }
                System.out.println("Processed message: " + message.getMessageId());
            } catch (Exception e) {
                System.err.println("Error processing message: " + message.getMessageId() + ", " + e.getMessage());
            }
        }
        return null;
    }


    private void sendEmail(String recipient, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(recipient))
            .withMessage(new Message()
                .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(body)))
                .withSubject(new Content().withCharset("UTF-8").withData(subject)))
            .withSource("lishakarj@gmail.com");
        sesClient.sendEmail(request);
    }
}