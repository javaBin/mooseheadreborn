import {RegistrationInfoType} from "../ServerTypes";

interface ParticipantRegistrationProps {
    registrationInfo:RegistrationInfoType
}

const ParticipantRegistration:React.FC<ParticipantRegistrationProps> = ({registrationInfo}) => {
    return (
        <div>
            <h2>{registrationInfo.workshopName}</h2>
            {registrationInfo.startTime && registrationInfo.endTime && <p>{registrationInfo.startTime} - {registrationInfo.endTime}</p>}
            <p><b>{registrationInfo.registrationStatusText}</b></p>
        </div>
    );
}

export default ParticipantRegistration;