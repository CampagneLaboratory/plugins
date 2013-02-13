/*
 * Copyright (c) 2011  by Cornell University and the Cornell Research
 * Foundation, Inc.  All Rights Reserved.
 *
 * Permission to use, copy, modify and distribute any part of GobyWeb web
 * application for next-generation sequencing data alignment and analysis,
 * officially docketed at Cornell as D-5061 ("WORK") and its associated
 * copyrights for educational, research and non-profit purposes, without
 * fee, and without a written agreement is hereby granted, provided that
 * the above copyright notice, this paragraph and the following three
 * paragraphs appear in all copies.
 *
 * Those desiring to incorporate WORK into commercial products or use WORK
 * and its associated copyrights for commercial purposes should contact the
 * Cornell Center for Technology Enterprise and Commercialization at
 * 395 Pine Tree Road, Suite 310, Ithaca, NY 14850;
 * email:cctecconnect@cornell.edu; Tel: 607-254-4698;
 * FAX: 607-254-5454 for a commercial license.
 *
 * IN NO EVENT SHALL THE CORNELL RESEARCH FOUNDATION, INC. AND CORNELL
 * UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF
 * WORK AND ITS ASSOCIATED COPYRIGHTS, EVEN IF THE CORNELL RESEARCH FOUNDATION,
 * INC. AND CORNELL UNIVERSITY MAY HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *
 * THE WORK PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE CORNELL RESEARCH
 * FOUNDATION, INC. AND CORNELL UNIVERSITY HAVE NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.  THE CORNELL
 * RESEARCH FOUNDATION, INC. AND CORNELL UNIVERSITY MAKE NO REPRESENTATIONS AND
 * EXTEND NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR EXPRESS, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE, OR THAT THE USE OF WORK AND ITS ASSOCIATED COPYRIGHTS
 * WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
 */

package org.campagnelab.gobyweb.plugins.xml.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabien Campagne
 *         Date: 11/8/11
 *         Time: 12:54 PM
 */
public class OptionErrors {
    /**
     * The list of option identifiers involved in an error. Can be multiple ids when a validation rule is triggered
     * that references multiple options.
     */
    public List<String> optionIdsInvolved = new ArrayList<String>();
    /**
     * The list of options that triggered an error and whose value should be reset.
     */
    public List<OptionError> optionsToReset = new ArrayList<OptionError>();

    /**
     * Determine if errors were found during validation.
     * @return
     */
    public boolean hasErrors() {
        return optionsToReset.size() > 0 || optionIdsInvolved.size() > 0;
    }

    /**
     * Error messages generated when rules fail to validate.
     */
    public List<String>ruleErrorMessages=new ArrayList<String>();

}
