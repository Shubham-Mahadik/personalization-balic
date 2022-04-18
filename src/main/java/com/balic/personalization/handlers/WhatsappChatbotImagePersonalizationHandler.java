package com.balic.personalization.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shaft.framework.channel.RequestHandler;
import com.shaft.framework.commons.JsonUtil;
import com.shaft.framework.commons.Literals;
import com.shaft.framework.commons.ShaftConstants;
import com.shaft.framework.commons.ShaftUtils;
import com.shaft.framework.exceptions.ValidationException;
import com.shaft.framework.http.JsonHttpPayload;
import com.shaft.framework.http.SerializedRequestJson;
import com.shaft.framework.models.ConfigModel;
import com.shaft.magick.services.CommandProcessService;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

@Component(service = RequestHandler.class,immediate = true,property = {
        ShaftConstants.SHAFT_HANDLER_SELECTOR+ "=whatsapp-chatbot-img-personalization",
        ShaftConstants.SHAFT_HANDLER_TYPE+ "=balic-whatsappchatbot",
        ShaftConstants.SHAFT_HANDLER_HTTP_METHOD+"="+ HttpConstants.METHOD_POST,
        ShaftConstants.SHAFT_HANDLER_PROTOCOL+"="+ Literals.ApiProtocol.JSON
})
public class WhatsappChatbotImagePersonalizationHandler implements RequestHandler {

private static final Logger LOGGER = LoggerFactory.getLogger(WhatsappChatbotImagePersonalizationHandler.class);

    @Reference
    CommandProcessService commandProcessService;

    private static final JsonUtil jsonUtil = JsonUtil.getInstance();
    Map<String, ? super String> properties;

    @Override
    public String createRequest(SerializedRequestJson request) {

    JsonObject headerJson = jsonUtil.getIfJsonObject.apply(request.getHeader().getAsJsonObject());
    JsonObject bodyObject= jsonUtil.getIfJsonObject.apply(request.getBody()).getAsJsonObject();

    String token = jsonUtil.getIfJsonPrimitive.apply(headerJson.get("authToken")).getAsString();
    JsonArray criteria = jsonUtil.getIfJsonArray.apply((headerJson.get("criteria"))).getAsJsonArray();

    String policyNo = jsonUtil.getIfJsonPrimitive.apply(bodyObject.get("policyNo")).getAsString();
    JsonObject policyDetails = jsonUtil.getIfJsonObject.apply(bodyObject.get("policyDetails")).getAsJsonObject();
    JsonArray fundDetails = jsonUtil.getIfJsonArray.apply(bodyObject.get("fundDetails")).getAsJsonArray();

    String policyImg=" ";
    String fundImg =" ";

        for(JsonElement criteriaEle: criteria) {
            String criteriaVal = jsonUtil.getIfJsonPrimitive.apply(criteriaEle).getAsString();
            if (criteriaVal.equals("policyDetailsImage")) {

                Map<String, String> configMap =(Map<String, String>) ConfigModel.getInstance().getConfigMap().get("balicWhatsAppChatBot_Model");
                String srcImgPath = configMap.get("policyImageTemplatePath");
                String personalisedPathPrefix = configMap.get("personalisedImagePath");
                String destImgPath = personalisedPathPrefix + "policyImg-" +policyNo+ srcImgPath.substring(srcImgPath.lastIndexOf("."));

                String policyImageCommand = generatePolicyImageCommand(policyNo,policyDetails,srcImgPath,destImgPath);
            try {
                String policyStatus = commandProcessService.execCmd(policyImageCommand);
                byte[] image = Files.readAllBytes(Paths.get(destImgPath));
                policyImg = Base64.getEncoder().encodeToString(image);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (criteriaVal.equals("fundDetailsImage")) {

                Map<String, String> configMap =(Map<String, String>) ConfigModel.getInstance().getConfigMap().get("balicWhatsAppChatBot_Model");
                String srcImgPath = configMap.get("fundImageTemplatePath");
                String personalisedPathPrefix = configMap.get("personalisedImagePath");
                String destImgPath = personalisedPathPrefix + "Fund-img-" +policyNo+ srcImgPath.substring(srcImgPath.lastIndexOf("."));

                String fundImageCommand = generateFundDetailsCommand(policyNo, fundDetails,policyDetails,srcImgPath,destImgPath);
            try {
                String fundStatus = commandProcessService.execCmd(fundImageCommand);
                byte[] image1 = Files.readAllBytes(Paths.get(destImgPath));
                fundImg = Base64.getEncoder().encodeToString(image1);
                } catch (IOException | InterruptedException e) {
                   e.printStackTrace();
                }
            }
        }

    JsonObject responseObj = new JsonObject();
    responseObj.addProperty("policyImg", policyImg);
    responseObj.addProperty("fundImg", fundImg);
    return JsonHttpPayload.Builder.builder().setBody(responseObj).toString();
    }

    private String generatePolicyImageCommand(String policyNo, JsonObject policyDetails, String srcImgPath, String destImgPath) {

        String insuredPersonName = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("insuredPersonName")).getAsString();
        String policyHolderName = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("policyHolderName")).getAsString();
        String policyStartDate = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("policyStartDate")).getAsString();
        String productName = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("productName")).getAsString();
        String premiumAmount = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("premiumAmount")).getAsString();
        String nextPremiumDate = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("nextPremiumDate")).getAsString();
        String policyTerm = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("policyTerm")).getAsString();
        String lastPremiumDate = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("lastPremiumDate")).getAsString();
        String policyStatus = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("policyStatus")).getAsString();
        String paymentMethod = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("paymentMethod")).getAsString();
        String premiumPaid = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("premiumPaid")).getAsString();
        String premiumPaymentTerm = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("premiumPaymentTerm")).getAsString();
        String policyEndDate = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("maturityDate")).getAsString();
        String productId = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("productId")).getAsString();
        String surrenderVal = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("surrenderVal")).getAsString();
        String productType = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("productType")).getAsString();
        String frequency = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("frequency")).getAsString();
        String ipDob = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("ipDob")).getAsString();
        String phAddress = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("phAddress")).getAsString();
        String nominee= jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("nominee")).getAsString();
        String panNo= jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("panNo")).getAsString();
        String fundPortfolio= jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("fundPortfolio")).getAsString();
        String variant = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("variant")).getAsString();
        String lifeCover = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("sumInsured")).getAsString();
        String policyTermYear = "";
        try{
            policyTermYear= Integer.parseInt(policyTerm)>0?"years":"year";
        } catch(NumberFormatException e ){
            LOGGER.error(ShaftUtils.writeException(e));
        }

        String premiumPaymentTermYear= "";
        try{
            premiumPaymentTermYear= Integer.parseInt(premiumPaymentTerm)>0?"years":"year";
        } catch (NumberFormatException e) {
            LOGGER.error(ShaftUtils.writeException(e));
        }

        String command = "convert -fill \"#f2f4f7\" " +
                "-pointsize 32 -font \"Arial-Bold\" -draw \"text 57,131 '" + productName + " '\" " +
                "-pointsize 31 -font \"AllianzSans-Bold\" " +
                "-gravity northwest " +
                " -fill \"#0072bc\" " +
                "-pointsize 18 -font \"AllianzSans-Bold\" " +
                "-gravity northwest " +
                "-draw \"text 340,280 '" + policyHolderName + " '\" " +
                "-draw \"text 340,325 '" + insuredPersonName + " '\" " +
                "-draw \"text 340,369 '" + policyNo + " '\" " +
                "-pointsize 20 " +
                "-draw \"text 340,410 '" + policyStatus + "'\" " +
                "-pointsize 20 " +
                "-draw \"text 340,453 '" +"₹"+ premiumAmount + "'\" " +
                "-draw \"text 340,494 '" + premiumPaid + "'\" " +
                "-draw \"text 340,537 '" + paymentMethod + "'\" " +
                "-draw \"text 405,830 '" + policyTerm + " "+policyTermYear+"' \" " +
                "-draw \"text 405,875 '" + premiumPaymentTerm + " "+premiumPaymentTermYear+"' \" " +
                "-pointsize 20 " +
                "-draw \"text 90,1020 '" + policyStartDate + "'\"  " +
                "-draw \"text 240,1020 '" + nextPremiumDate + "'\" " +
                "-draw \"text 390,1020 '" + lastPremiumDate + "'\"  " +
                "-draw \"text 540,1020 '" + policyEndDate + "'\" " +
                "-fill \"#ffffff\" " +
                "-pointsize 40 " +
                "-draw \"text 390,685 '" +"₹"+ lifeCover + "' \"  " + srcImgPath + " " + destImgPath;
        return command;
    }

    private static final Function<String, String> wrapQuotes = str -> Objects.nonNull(str) ? "'" + str + "'" : str;
    private String generateFundDetailsCommand(String policyNo, JsonArray fundDetails, JsonObject policyDetails, String srcImgPath, String destImgPath) {

        String policyHolderName = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("policyHolderName")).getAsString();
        String fundValue = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("sumInsured")).getAsString();
        String premiumAmount = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("premiumAmount")).getAsString();
        String productName = jsonUtil.getIfJsonPrimitive.apply(policyDetails.get("productName")).getAsString();
        String fundTableStr = "";
        int yCoordinate = 675;
        String tableRowTemplate = "-pointsize 16 -draw \"text 80,{0} {1}\" -pointsize 18 -draw \"text 370,{2} {3}\" -pointsize 18 -draw \"text 555,{4} {5}\"";
        StringJoiner tableJoiner = new StringJoiner(" ");
        for (JsonElement fundEle : fundDetails) {
            JsonObject fundObj = fundEle.getAsJsonObject();
            String fundName = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("fundName")).getAsString();
            String tableFundValue = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("regularPremiumFundVal")).getAsString();
            String fundAllocation = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("unitPrice")).getAsString();
            String topupPremiumUnit = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("topupPremiumUnit")).getAsString();
            String RegularPremiumUnit = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("RegularPremiumUnit")).getAsString();
            String topupPremiumFundVal = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("topupPremiumFundVal")).getAsString();
            String fundId = jsonUtil.getIfJsonPrimitive.apply(fundObj.get("fundId")).getAsString();
            yCoordinate = yCoordinate + 45;
            String rowStr = MessageFormat.format(tableRowTemplate,yCoordinate, wrapQuotes.apply(fundName), yCoordinate, wrapQuotes.apply("₹"+ tableFundValue), yCoordinate, wrapQuotes.apply(fundAllocation));
            tableJoiner.add(rowStr);
        }

        String command = "convert -fill \"#f2f4f7\" -pointsize 25 -font \"AllianzSans-Bold\" -gravity northwest " +
                "-draw \"text 318,355 '" + policyHolderName + "'\" " +
                "-draw \"text 248,400 ' " + policyNo + "'\" " +
                "-draw \"text 300,559 ' " +"₹"+ fundValue + "'\" " +
                "-pointsize 33 -font \"Arial-Bold\" -draw \"text 63,170 '" + productName + "'\" " +
                "-pointsize 25 -font \"AllianzSans-Bold\" -draw \"text 320,450 ' "+"₹" + premiumAmount + "'\" " + tableJoiner + " " + srcImgPath + " " + destImgPath;
        return command;

    }
    @Override
    public boolean validateRequest(SerializedRequestJson request){

       JsonObject jsonObject = jsonUtil.getIfJsonObject.apply(request.getBody()).getAsJsonObject();
       String policyNo = jsonUtil.getIfJsonPrimitive.apply(jsonObject.get("policyNo")).getAsString();
       if(policyNo.isEmpty()){
        throw new ValidationException("Policy Number can't be empty",-1,false);
       }
       return true;
    }

    @Override
    public void setProperties(Map<String, ? super  String>properties) {
        this.properties= properties;
    }

    public Map<String, ? super String>getProperties() {
        return this.properties;
    }
}

