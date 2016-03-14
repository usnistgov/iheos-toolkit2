/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package gov.nist.toolkit.saml.util;

import org.opensaml.common.SAMLVersion;

import gov.nist.toolkit.saml.builder.bean.KeyInfoBean;
import gov.nist.toolkit.saml.builder.bean.SubjectBean;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SAML2CallbackHandler extends AbstractSAMLCallbackHandler {
    
    public SAML2CallbackHandler() throws Exception {
       /* if (certs == null) {
            Crypto crypto = CryptoFactory.getInstance("wss40.properties");
            CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
            cryptoType.setAlias("wss40");
            certs = crypto.getX509Certificates(cryptoType);
        }*/
        
        subjectName = "uid=joe,ou=people,ou=saml-demo,o=example.com";
        subjectQualifier = "www.example.com";
        confirmationMethod = SamlConstants.CONF_SENDER_VOUCHES;
    }
    
    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof SAMLCallback) {
                SAMLCallback callback = (SAMLCallback) callbacks[i];
                callback.setSamlVersion(SAMLVersion.VERSION_20);
                callback.setIssuer(issuer);
                SubjectBean subjectBean = 
                    new SubjectBean(
                        subjectName, subjectQualifier, confirmationMethod
                    );
                if (subjectNameIDFormat != null) {
                    subjectBean.setSubjectNameIDFormat(subjectNameIDFormat);
                }
                if (SamlConstants.CONF_HOLDER_KEY.equals(confirmationMethod)) {
                    try {
                        //KeyInfoBean keyInfo = createKeyInfo();
                        //subjectBean.setKeyInfo(keyInfo);
                    } catch (Exception ex) {
                        throw new IOException("Problem creating KeyInfo: " +  ex.getMessage());
                    }
                }
                callback.setSubject(subjectBean);
                createAndSetStatement(null, callback);
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
    
}
