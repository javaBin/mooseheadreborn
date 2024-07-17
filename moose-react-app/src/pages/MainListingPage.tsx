import { Container } from "react-bootstrap";
import {WorkshopType} from "../ServerTypes";
import { useEffect, useState } from "react";
import ServerConfig from "../ServerConfig";
import WorkshopInfo from "../components/WorkshopInfo";

function MainListingPage() {
    const [workshopList,setWorkshopList] = useState<WorkshopType[]>([]);
    useEffect(() => {
        fetch(ServerConfig.address + "/api/workshopList")
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
            }).then(json => setWorkshopList(json))

    }, []);
    return (
        <Container>
            <h1>Workshops</h1>
            {workshopList.map((workshop) => <WorkshopInfo workshop={workshop}/>) }
        </Container>
    );
}

export default MainListingPage;