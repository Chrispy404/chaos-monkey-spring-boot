/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Thorsten Deelmann
 */
@RunWith(MockitoJUnitRunner.class)
public class LatencyAssaultTest {

    @Mock
    private ChaosMonkeySettings chaosMonkeySettings;

    @Mock
    private AssaultProperties assaultProperties;


    @Test
    public void threadSleepHasBeenCalled() {
        int latencyRangeStart = 100;
        int latencyRangeEnd = 200;
        TestLatencyAssaultExecutor executor = new TestLatencyAssaultExecutor();

        when(assaultProperties.getLatencyRangeStart()).thenReturn(latencyRangeStart);
        when(assaultProperties.getLatencyRangeEnd()).thenReturn(latencyRangeEnd);
        when(chaosMonkeySettings.getAssaultProperties()).thenReturn(assaultProperties);

        LatencyAssault latencyAssault = new LatencyAssault(chaosMonkeySettings, null, executor);
        latencyAssault.attack();

        assertTrue(executor.executed);
        String assertionMessage = "Latency not in range 100-200, actual latency: " +executor.duration;
        assertTrue(assertionMessage, executor.duration >= latencyRangeStart);
        assertTrue(assertionMessage,executor.duration <= latencyRangeEnd);
    }


    public class TestLatencyAssaultExecutor implements ChaosMonkeyLatencyAssaultExecutor {

        private long duration;
        private boolean executed;

        TestLatencyAssaultExecutor() {
            this.duration = 0;
            this.executed = false;
        }

        @Override
        public void execute(long duration) {
            this.duration = duration;
            this.executed = true;
        }
    }
}