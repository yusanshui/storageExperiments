package org.example;


import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        KubernetesClient kubernetesClient = new DefaultKubernetesClient();
        PodList list = kubernetesClient.pods().inAnyNamespace().list();
        List<Pod> items = list.getItems();
        for(Pod pod : items){
            System.out.println(pod.getSpec().getNodeName());
        }
    }
}