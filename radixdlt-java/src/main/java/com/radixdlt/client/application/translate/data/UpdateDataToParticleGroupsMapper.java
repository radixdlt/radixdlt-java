/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.radixdlt.client.application.translate.data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.radixdlt.client.application.translate.ShardedParticleStateId;
import com.radixdlt.client.application.translate.StageActionException;
import com.radixdlt.client.application.translate.StatefulActionToParticleGroupsMapper;
import com.radixdlt.client.atommodel.cru.CRUDataParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.identifiers.RadixAddress;



public class UpdateDataToParticleGroupsMapper implements StatefulActionToParticleGroupsMapper<UpdateDataAction> {

    @Override
    public Set<ShardedParticleStateId> requiredState(UpdateDataAction updateDataAction) {
        RadixAddress addresss = updateDataAction.getRRI().getAddress();
        return ImmutableSet.of(ShardedParticleStateId.of(CRUDataParticle.class, addresss));
    }

    @SuppressWarnings("serial")
    @Override
    public List<ParticleGroup> mapToParticleGroups(UpdateDataAction updateDataAction, Stream<Particle> store) throws StageActionException {
        RRI rri = updateDataAction.getRRI();
        List<CRUDataParticle> records = store.filter(p -> p instanceof CRUDataParticle)
                .map(CRUDataParticle.class::cast)
                .filter(p -> p.rri().equals(rri))
                .collect(Collectors.toList());
        if (records.size() != 1) {
            throw new StageActionException("Broken storage") { };
        }
        CRUDataParticle prevData = records.get(0);
        CRUDataParticle newData = new CRUDataParticle(rri, prevData.serialno() + 1, updateDataAction.getData());
        ParticleGroup particleGroup = ParticleGroup.of(SpunParticle.down(prevData), SpunParticle.up(newData));
        return Collections.singletonList(particleGroup);
    }
}
