package com.example.ais_store.Processor;

import com.example.ais_store.Responses.Response;
import com.example.ais_store.Requests.Command;
import com.example.ais_store.DB.Categories;
import com.example.ais_store.DB.Products;
import com.example.ais_store.Message.MessageInfo;
import com.example.ais_store.Models.Category;
import com.example.ais_store.Models.Product;
import com.example.ais_store.Packet.Packet;
import com.example.ais_store.Packet.PacketInfo;

import java.util.ArrayList;

public class Processor {
    public static PacketInfo process(PacketInfo packetInfo) throws Exception {
        int response = -1;
        String responseMessage = "";

        Command command = Command.values()[packetInfo.bMsg.cType];

        switch (command) {
            case ADD_PRODUCT -> {
                String[] parameters = new String(packetInfo.bMsg.message).split("\\$");

                if (Categories.getById(Integer.parseInt(parameters[1])) == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category does`t exist";
                    break;
                }

                Product product = Products.getByName(parameters[0]);
                if (product != null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product`s name already exists";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case ADD_CATEGORY -> {
                String name = new String(packetInfo.bMsg.message);

                int categoryId = Categories.getCategoryIdByName(name);

                if (categoryId != -1) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category`s name already exists";
                    break;
                }

                response = Response.OK.ordinal();

            }
            case DELETE_PRODUCT_BY_ID -> {
                int id = Integer.parseInt(new String(packetInfo.bMsg.message));
                Product product = Products.getById(id);
                if (product == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product doesn`t exist";
                } else {
                    response = Response.OK.ordinal();
                }
            }
            case DELETE_CATEGORY_BY_ID -> {
                int id = Integer.parseInt(new String(packetInfo.bMsg.message));

                Category category = Categories.getById(id);
                if (category == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category doesn`t exist";
                    break;
                }

                ArrayList<Product> products = Products.getAllFromCategory(id);
                if (products.size() != 0) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category has products";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_PRODUCT_ID -> {
                String[] parameters = new String(packetInfo.bMsg.message).split(" ");
                int oldId = Integer.parseInt(parameters[0]);
                int newId = Integer.parseInt(parameters[1]);

                Product product = Products.getById(oldId);
                if (product == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product doesn`t exist";
                    break;
                }

                product = Products.getById(newId);
                if (product != null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This id already exists";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_PRODUCT_NAME -> {
                String[] parameters = new String(packetInfo.bMsg.message).split("\\$");
                int id = Integer.parseInt(parameters[0]);
                String name = parameters[1];

                Product product = Products.getById(id);
                if (product == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product doesn`t exist";
                    break;
                }

                product = Products.getByName(name);
                if (product != null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product`s name already exists";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_PRODUCT_CATEGORY -> {
                String[] parameters = new String(packetInfo.bMsg.message).split(" ");
                int productId = Integer.parseInt(parameters[0]);
                int newCategoryId = Integer.parseInt(parameters[1]);

                Product product = Products.getById(productId);
                if (product == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product doesn`t exist";
                    break;
                }

                Category category = Categories.getById(newCategoryId);
                if (category == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category doesn`t exist";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_PRODUCT_PRICE -> {
                String[] parameters = new String(packetInfo.bMsg.message).split(" ");

                int id = Integer.parseInt(parameters[0]);
                //double price = Double.parseDouble(parameters[1]);

                Product product = Products.getById(id);
                if (product == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product doesn`t exist";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_PRODUCT_NUMBER -> {
                String[] parameters = new String(packetInfo.bMsg.message).split(" ");
                if (parameters.length != 2) {
                    throw new Exception("Incorrect parameters");
                }

                int id = Integer.parseInt(parameters[0]);
                //int newNumber = Integer.parseInt(parameters[1]);

                Product product = Products.getById(id);
                if (product == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This product doesn`t exist";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_CATEGORY_ID -> {
                String[] parameters = new String(packetInfo.bMsg.message).split(" ");
                int oldId = Integer.parseInt(parameters[0]);
                int newId = Integer.parseInt(parameters[1]);

                Category category = Categories.getById(oldId);
                if (category == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category doesn`t exist";
                    break;
                }

                category = Categories.getById(newId);
                if (category != null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This id already exists";
                    break;
                }

                response = Response.OK.ordinal();
            }
            case UPDATE_CATEGORY_NAME -> {
                String[] parameters = new String(packetInfo.bMsg.message).split("\\$");

                int id = Integer.parseInt(parameters[0]);
                String newName = parameters[1];

                Category category = Categories.getById(id);
                if (category == null) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This category doesn`t exist";
                    break;
                }

                int categoryId = Categories.getCategoryIdByName(newName);
                if (categoryId != -1) {
                    response = Response.ERROR.ordinal();
                    responseMessage = "This name already exists";
                    break;
                }

                response = Response.OK.ordinal();
            }
            default -> responseMessage = "No such command";
        }

        return new PacketInfo(
                packetInfo.bSrc,
                Packet.getFreePacketId(),
                packetInfo.wLen,
                new MessageInfo(
                        response,
                        packetInfo.bMsg.bUserId,
                        responseMessage.getBytes()));
    }
}
