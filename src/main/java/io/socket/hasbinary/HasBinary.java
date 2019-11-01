package io.socket.hasbinary;

import java.util.Iterator;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HasBinary {
	
    private static final Logger logger = Logger.getLogger(HasBinary.class.getName());
	
    private HasBinary() {}

    public static boolean hasBinary(Object data) {
        return _hasBinary(data);
    }

    private static boolean _hasBinary(Object obj) {
        if (obj == null) return false;

        if (obj instanceof byte[]) {
            return true;
        }

        if (obj instanceof ArrayNode) {
            ArrayNode _obj = (ArrayNode) obj;
            int length = _obj.size();
            for (int i = 0; i < length; i++) {
                Object v;
                v = _obj.get(i).isNull() ? null : _obj.get(i);
                if (_hasBinary(v)) {
                    return true;
                }
            }
        } else if (obj instanceof ObjectNode) {
            ObjectNode _obj = (ObjectNode) obj;
            Iterator keys = _obj.fieldNames();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                Object v;
                v = _obj.get(key);
                if (_hasBinary(v)) {
                    return true;
                }
            }
        }

        return false;
    }
}
