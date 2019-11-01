package io.socket.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static de.comroid.util.json.JsonSupport.nodeOf;

public class Binary {

    private static final String KEY_PLACEHOLDER = "_placeholder";

    private static final String KEY_NUM = "num";
    
    private static final Logger logger = Logger.getLogger(Binary.class.getName());

    @SuppressWarnings("unchecked")
    public static DeconstructedPacket deconstructPacket(Packet packet) {
        List<byte[]> buffers = new ArrayList<byte[]>();

        packet.data = _deconstructPacket(packet.data, buffers);
        packet.attachments = buffers.size();

        DeconstructedPacket result = new DeconstructedPacket();
        result.packet = packet;
        result.buffers = buffers.toArray(new byte[buffers.size()][]);
        return result;
    }

    private static Object _deconstructPacket(Object data, List<byte[]> buffers) {
        if (data == null) return null;

        if (data instanceof byte[]) {
            ObjectNode placeholder = JsonNodeFactory.instance.objectNode();
            placeholder.put(KEY_PLACEHOLDER, true);
            placeholder.put(KEY_NUM, buffers.size());
            buffers.add((byte[])data);
            return placeholder;
        } else if (data instanceof ArrayNode) {
            ArrayNode newData = JsonNodeFactory.instance.arrayNode();
            ArrayNode _data = (ArrayNode) data;
            int len = _data.size();
            for (int i = 0; i < len; i ++) {
                newData.set(i, nodeOf(_deconstructPacket(_data.get(i), buffers)));
            }
            return newData;
        } else if (data instanceof ObjectNode) {
            ObjectNode newData = JsonNodeFactory.instance.objectNode();
            ObjectNode _data = (ObjectNode) data;
            Iterator<?> iterator = _data.fieldNames();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                newData.set(key, nodeOf(_deconstructPacket(_data.get(key), buffers)));
            }
            return newData;
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public static Packet reconstructPacket(Packet packet, byte[][] buffers) {
        packet.data = _reconstructPacket(packet.data, buffers);
        packet.attachments = -1;
       return packet;
    }

    private static Object _reconstructPacket(Object data, byte[][] buffers) {
        if (data instanceof ArrayNode) {
            ArrayNode _data = (ArrayNode) data;
            int len = _data.size();
            for (int i = 0; i < len; i ++) {
                _data.set(i, nodeOf(_reconstructPacket(_data.get(i), buffers)));
            }
            return _data;
        } else if (data instanceof ObjectNode) {
            ObjectNode _data = (ObjectNode) data;
            if (_data.get(KEY_PLACEHOLDER).asBoolean(false)) {
                int num = _data.get(KEY_NUM).asInt(-1);
                return num >= 0 && num < buffers.length ? buffers[num] : null;
            }
            Iterator<?> iterator = _data.fieldNames();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                _data.set(key, nodeOf(_reconstructPacket(_data.get(key), buffers)));
            }
            return _data;
        }
        return data;
    }

    public static class DeconstructedPacket {

        public Packet packet;
        public byte[][] buffers;
    }
}


