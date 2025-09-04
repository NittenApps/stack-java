/*
 * Copyright (c) 2025. ANA SEGUROS
 */

package dev.nittenapps.stack.core.support;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.util.Set;

@Slf4j
public class PhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    @Serial private static final long serialVersionUID = 3039231820883264225L;

    private static final Set<String> KEYWORDS = Set.of("number");

    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment context) {
        return quoteIdentifier(super.toPhysicalColumnName(logicalName, context), context);
    }

    @Nullable
    private Identifier quoteIdentifier(Identifier physicalName, JdbcEnvironment context) {
        if (physicalName == null) {
            return null;
        }

        String name = physicalName.getText();
        if (KEYWORDS.contains(name)) {
            String quotedName = context.getDialect().quote('`' + name + '`');
            log.debug("Quoting identifier '{}', {}", name, quotedName);
            return Identifier.toIdentifier(quotedName);
        }
        return physicalName;
    }
}
