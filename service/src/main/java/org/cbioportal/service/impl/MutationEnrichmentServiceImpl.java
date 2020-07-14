package org.cbioportal.service.impl;

import org.apache.commons.math3.util.Pair;
import org.cbioportal.model.*;
import org.cbioportal.model.MolecularProfile.MolecularAlterationType;
import org.cbioportal.model.util.Select;
import org.cbioportal.service.AlterationCountService;
import org.cbioportal.service.MutationEnrichmentService;
import org.cbioportal.service.exception.MolecularProfileNotFoundException;
import org.cbioportal.service.util.AlterationEnrichmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MutationEnrichmentServiceImpl implements MutationEnrichmentService {

    @Autowired
    private AlterationCountService alterationCountService;
    @Autowired
    private AlterationEnrichmentUtil<AlterationCountByGene> alterationEnrichmentUtil;

    @Override
    public List<AlterationEnrichment> getMutationEnrichments(
        Map<String, List<MolecularProfileCaseIdentifier>> molecularProfileCaseSets,
        EnrichmentType enrichmentType,
        AlterationFilter alterationFilter) throws MolecularProfileNotFoundException {

        alterationEnrichmentUtil.validateMolecularProfiles(molecularProfileCaseSets,
                Arrays.asList(MolecularAlterationType.MUTATION_EXTENDED, MolecularAlterationType.MUTATION_UNCALLED),
                null);

        Map<String, Pair<List<AlterationCountByGene>, Long>> mutationCountsbyEntrezGeneIdAndGroup = getMutationCountsbyEntrezGeneIdAndGroup(
            molecularProfileCaseSets, enrichmentType, alterationFilter);

        return alterationEnrichmentUtil.createAlterationEnrichments(mutationCountsbyEntrezGeneIdAndGroup);
    }

    public Map<String, Pair<List<AlterationCountByGene>, Long>> getMutationCountsbyEntrezGeneIdAndGroup(
        Map<String, List<MolecularProfileCaseIdentifier>> molecularProfileCaseSets,
        EnrichmentType enrichmentType,
        AlterationFilter alterationFilter) {
        return molecularProfileCaseSets
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> { //set value of each group to list of MutationCountByGene
                    List<String> molecularProfileIds = new ArrayList<>();
                    List<String> sampleIds = new ArrayList<>();

                    entry.getValue().forEach(molecularProfileCase -> {
                        molecularProfileIds.add(molecularProfileCase.getMolecularProfileId());
                        sampleIds.add(molecularProfileCase.getCaseId());
                    });

                    if (enrichmentType.name().equals("SAMPLE")) {
                        return alterationCountService.getSampleMutationCounts(
                            entry.getValue(),
                            Select.all(),
                            true,
                            true,
                            alterationFilter);
                    } else {
                        return alterationCountService
                            .getPatientMutationCounts(
                                entry.getValue(),
                                Select.all(),
                                true,
                                true,
                                alterationFilter);
                    }
                }));
    }
}
