import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import ServerConfig from "../ServerConfig";
import {WorkshopType} from "../ServerTypes";
import WorkshopInfo from "../components/WorkshopInfo";

function WorkshopRegistryPage() {
    const { workshopId} = useParams<{workshopId: string}>();
    const [workshop, setWorkshop] = useState<WorkshopType | null>(null);
    const [ errormessage, setErrormessage] = useState<String | null>(null);

    useEffect(() => {
        fetch(ServerConfig.address + "/api/workshop/" + workshopId)//
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            }).then(json => setWorkshop(json))
            .catch(error => {
                setErrormessage("error in catch : " + error.message);
            });

    }, []);

    return (<Container>
        {errormessage && <div>Error: {errormessage}</div>}
        {!(workshop || errormessage) && <div>Loading</div>}
        {workshop && <WorkshopInfo workshop={workshop}/>}
    </Container>);
}

export default WorkshopRegistryPage;