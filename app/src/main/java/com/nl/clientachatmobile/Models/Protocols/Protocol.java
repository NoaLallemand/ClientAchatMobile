package com.nl.clientachatmobile.Models.Protocols;

import com.nl.clientachatmobile.Models.Requests.Request;
import com.nl.clientachatmobile.Models.Responses.Response;

import java.io.IOException;

public interface Protocol {

    Response handleRequest(Request request) throws Exception;
}
