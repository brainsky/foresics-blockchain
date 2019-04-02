/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/**
 * A evidence has been received by an importer 
 * @param {org.cpsec.forensicsnetwork.EvidenceReceived} evidenceReceived
 * @transaction
 */
async function storeEvidence(evidenceReceived){

        const NS = 'org.cpsec.forensicsnetwork';

        let evidence = evidenceReceived.evidence;

        let notary = evidenceReceived.notary;
        //set the status of the evidence
        evidence.status = 'STORED';
        //set NotaryParty
        evidence.storeCompany = notary;

        let evidenceResistry = await getAssetRegistry(NS + ".Evidence");

        evidenceResistry.update(evidence);

}


/**
 * A evidence reading has been received
 * @param {org.cpsec.forensicsnetwork.EvidenceReading} evidenceReading
 * @transaction
 */
async function evidenceReading(evidenceReading){

        const NS = 'org.cpsec.forensicsnetwork';

        let evidence = evidenceReading.evidence;

}

/**
 * Arbitration Forensics.
 * @param {org.cpsec.forensicsnetwork.Forensics} forensics
 * @transaction
 */
async function forensics(forensics){

        const NS = 'org.cpsec.forensicsnetwork';

        let evidence = forensics.evidence;

        let notaryParty = forensics.notaryParty;

        let arbitrator = forensics.arbitrator;

        evidence.arbitrator = arbitrator;
        
        //if arbitrator check evidence store in the notary party is true,
        // then set evidence status is VALID.
        if(evidence.storeCompany.companyName 
                == notaryParty.companyName){

                evidence.status = "VALID"
      
        }else{

                evidence.status = "INVALID"
        }

        let evidenceRegistry = await getAssetRegistry(NS + ".Evidence");

        evidenceRegistry.update(evidence);


        


}