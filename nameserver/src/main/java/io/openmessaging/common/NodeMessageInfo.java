package io.openmessaging.common;

import io.openmessaging.table.MessageInfo;

/**
 * Created by fbhw on 17-12-12.
 */
public class NodeMessageInfo {

        private Object value;
        private NodeMessageInfo next;

        public NodeMessageInfo(Object value) {

            this.value = value;
        }



        public NodeMessageInfo getNext() {
            return next;
        }

        public void setNext(NodeMessageInfo next) {
            this.next = next;
        }


        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

