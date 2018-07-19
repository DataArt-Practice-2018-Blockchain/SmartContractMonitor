package blockchainlogger.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlockchainRouter extends RouteBuilder {

    @Value("${node.address}")
    private String nodeAddress;

    @Value("${node.port}")
    private int nodePort;

    @Override
    public void configure() throws Exception {
        /*from("web3j://http://"
                + nodeAddress
                + ":"
                + Integer.toString(nodePort)
                + "?operation=BLOCK_OBSERVABLE&fullTransactionObjects=true")
                */
        from("web3j://http://35.228.59.11:8545?operation=BLOCK_OBSERVABLE&fullTransactionObjects=true")
                .marshal().json(JsonLibrary.Gson)
                .convertBodyTo(String.class)
                .to("stream:out")
                .to("mongodb:mongo?database=blockchain&collection=blocks&operation=insert");
    }
}
