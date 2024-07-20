import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import WorkshopRegistrationComponent from "../components/WorkshopRegistrationComponent";

function WorkshopRegistryPage() {
    const { workshopId} = useParams<{workshopId: string}>();


    return (<Container>
        {workshopId && <WorkshopRegistrationComponent workshopId={workshopId} accessToken={null}/>}
    </Container>);
}

export default WorkshopRegistryPage;