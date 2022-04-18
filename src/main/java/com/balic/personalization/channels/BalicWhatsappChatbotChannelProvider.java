package com.balic.personalization.channels;

import com.shaft.framework.channel.ChannelProvider;
import com.shaft.framework.channel.RequestHandler;
import com.shaft.framework.channel.ServiceFactory;
import com.shaft.framework.commons.ShaftConstants;
import com.shaft.framework.exceptions.ServiceException;
import com.shaft.framework.http.SerializedRequestJson;
import com.shaft.framework.models.ConfigModel;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Designate(ocd =BalicWhatsappChatbotChannelProvider.Config.class)
@Component(service = ChannelProvider.class,immediate = true,property = {
        ShaftConstants.SHAFT_CHANNEL_NAME+"="+"balic-whatsappchatbot"
})
public class BalicWhatsappChatbotChannelProvider implements ChannelProvider {

       @ObjectClassDefinition(name = "Balic Whatsapp Chatbot Configuration")
        public @interface Config {
        @AttributeDefinition(name = "Policy Image Template Path", description = "Policy Image Path is provided here")
        String policyImageTemplatePath() default "/opt/balic/whatsappchatbot/src-imgs/policy-template.png";

        @AttributeDefinition(name = "Fund Image Template Path",description = "Fund Image template is provided here")
        String fundImageTemplatePath() default "/opt/balic/whatsappchatbot/src-imgs/fund-template.png";

        @AttributeDefinition(name = "Personalised Image Path", description = "Personalised Image Path is provided here")
        String personaliseImageTemplatePath() default "/opt/balic/whatsappchatbot/dest-imgs/";
    }

    public Map<String , String> contractMap;

    @Modified
    @Activate
    public void activate(Config config){
        Map<String, Object> ConfigMap = new HashMap<>();
        this.contractMap= new HashMap<>();
        contractMap.put("policyImageTemplatePath",config.policyImageTemplatePath());
        contractMap.put("fundImageTemplatePath",config.fundImageTemplatePath());
        contractMap.put("personalisedImagePath",config.personaliseImageTemplatePath());
        ConfigMap.put("balicWhatsAppChatBot_Model",contractMap);
        ConfigModel.getInstance().updateConfigMap(ConfigMap);
    }

    @Reference
    ServiceFactory serviceFactory;

    @Override
    public String call(SerializedRequestJson serializedRequestJson) throws ServiceException {
        String apiName = serializedRequestJson.getAPIName();
        RequestHandler requestHandler = serviceFactory.getRequestHandler(apiName);

        if(Objects.isNull(requestHandler)) {
            throw new ServiceException("Invalid API Name",-1,false);
        }
        if (requestHandler.validateRequest(serializedRequestJson)) {
            return requestHandler.createRequest(serializedRequestJson);
        }
        throw new ServiceException("Validation",-1,false);
    }
}
