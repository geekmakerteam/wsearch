package org.github.pister.wsearch.core.context;

import org.github.pister.wsearch.core.schema.Schema;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 下午5:38
 */
public interface AppContext {


    Schema getSchema(String name);

}
