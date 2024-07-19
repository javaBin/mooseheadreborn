import { Container } from "react-bootstrap";
import {WorkshopType} from "../ServerTypes";
import { useEffect, useState } from "react";
import ServerConfig from "../ServerConfig";
import WorkshopDisplay from "../components/WorkshopDisplay";

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
            {workshopList.map((workshop) => <WorkshopDisplay key={workshop.id} workshop={workshop} displayLink={true}/>) }
        </Container>
    );
}

export default MainListingPage;