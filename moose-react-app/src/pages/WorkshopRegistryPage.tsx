import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";

function WorkshopRegistryPage() {
    const { workshopId} = useParams<{workshopId: string}>();
    return (<Container>
        <h1>Workshop {workshopId}</h1>
    </Container>);
}

export default WorkshopRegistryPage;