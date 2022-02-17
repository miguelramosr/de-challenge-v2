package com.personal.epl.domain;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EPLSeasonHighestScorers extends AbstractEPLDomain implements Serializable {
    LinkedHashMap<String, Integer> results;


    @Override
    public String toString() {
        int position = 1;
        List<DomainBuilder> domainList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: results.entrySet()) {
            DomainBuilder domain =  new DomainBuilder.AuxDomainBuilder(entry.getKey(), position).goals(entry.getValue()).build();
            domainList.add(domain);
            position++;
        }
        return new Gson().toJsonTree(domainList.get(0)).toString();
    }
}
