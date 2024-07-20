import {AddParticipantInput, WorkshopType} from "../ServerTypes";
import RegisterParticipant from "./RegisterParticipant";
import WorkshopDisplay from "./WorkshopDisplay";
import {useEffect, useState} from "react";
import ServerConfig from "../ServerConfig";

interface WorkshopInfoProps {
    workshopId:string,
    accessToken:string|null,
}
const WorkshopRegistrationComponent: React.FC<WorkshopInfoProps> = ({workshopId,accessToken}) => {
    const [workshop, setWorkshop] = useState<WorkshopType>();


    const handleRegisterParticipant:(addParticipantInput:AddParticipantInput)=>Promise<string|null> = (participant: AddParticipantInput)  => {
        return new Promise((resolve,reject) => {

        });
    };

    useEffect(() => {
        ServerConfig.readWorkshopFromServer(workshopId).then(resultFromServer => {
                if (resultFromServer.workshop) {
                    setWorkshop(resultFromServer.workshop);
                }
            }
        )
    }, []);

    return (<div>
        <h1>Registration</h1>
        {workshop && <WorkshopDisplay workshop={workshop} displayLink={false}/>}

        <div>
            <RegisterParticipant onRegisterParticipant={handleRegisterParticipant}/>
        </div>
    </div>);
}

export default WorkshopRegistrationComponent;