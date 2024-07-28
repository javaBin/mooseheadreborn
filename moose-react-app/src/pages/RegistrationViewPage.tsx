import {Alert, Container} from "react-bootstrap";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {RegistrationStatus, WorkshopInfoFromServer} from "../ServerTypes";
import readRegistrationFromServer from "../hooks/readRegistrationFromServer";
import WorkshopDisplay from "../components/WorkshopDisplay";
import WorkshopCancellation from "../components/WorkshopCancellation";

function RegistrationViewPage() {
    const { registrationId} = useParams<{registrationId: string}>();
    const [errormessage, setErrormessage] = useState<string|null>(null);
    const [workshopInfo,setWorkshopInfo]= useState<WorkshopInfoFromServer|null>(null);

    useEffect(() => {
        if (registrationId) {
            readRegistrationFromServer(registrationId)
                .then(workshopInfoFromServer => setWorkshopInfo(workshopInfoFromServer))
                .catch(errorFromServer => setErrormessage(errorFromServer));
        }
    }, []);
    const onRegistrationCancelled = () => {}
    return (
        <Container>
            {errormessage && <Alert variant={"danger"}>{errormessage}</Alert> }
            {workshopInfo?.workshop && <WorkshopDisplay workshop={workshopInfo.workshop} displayLink={false}/>}
            {workshopInfo?.registrationStatus === RegistrationStatus.CANCELLED && <p>Registration is cancelled</p>}
            {(workshopInfo?.registrationStatus && workshopInfo.registrationStatus !== RegistrationStatus.CANCELLED && workshopInfo?.registrationId) &&
                <WorkshopCancellation accessToken={null} registrationId={workshopInfo.registrationId} onRegistrationCancelled={onRegistrationCancelled} registrationStatus={workshopInfo.registrationStatus} numRegistered={workshopInfo.numRegistered}/>
            }
        </Container>
    );
}

export default RegistrationViewPage;