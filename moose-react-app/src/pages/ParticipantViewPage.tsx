import {Alert, Container} from "react-bootstrap";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {ParticipantRegistrationType} from "../ServerTypes";
import readParticipantInfoFromServer from "../hooks/readParticipantInfoFromServer";
import ParticipantRegistration from "../components/ParticipantRegistration";

const ParticipantViewPage = () => {
    const [participantRegistration, setParticipantRegistration] = useState<ParticipantRegistrationType|null>(null);
    const [errormessage, setErrormessage] = useState<string|null>(null);
    const { participantId} = useParams<{participantId: string}>();

    useEffect(() => {
        if (participantId) {
            readParticipantInfoFromServer(participantId)
                .then(participantRegistrationFromServer=>setParticipantRegistration(participantRegistrationFromServer))
                .catch(errorFromServer => setErrormessage(errorFromServer));
        }
    },[participantId]);

    return (<Container>
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert> }
        {participantRegistration && <div>
            <h1>{participantRegistration.participantName}</h1>
            {participantRegistration.registrationInfoList.map((registrationInfo,index) => <ParticipantRegistration registrationInfo={registrationInfo} key={index}></ParticipantRegistration>)}
        </div>}
    </Container>)
}

export default ParticipantViewPage;