package com.rb.api_chat.event;

import com.rb.api_chat.model.BaseDocument;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BaseDocumentListener extends AbstractMongoEventListener<BaseDocument> {

    @Override
    public void onBeforeConvert(
            BeforeConvertEvent<BaseDocument> event) {

        BaseDocument entity = event.getSource();

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
    }

}