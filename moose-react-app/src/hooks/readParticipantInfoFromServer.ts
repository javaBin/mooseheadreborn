import {ParticipantRegistrationType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const readParticipantInfoFromServer = (participantId:string):Promise<ParticipantRegistrationType> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/participant/" + participantId)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            }).then(json => resolve(json))
            .catch(error => {
                reject(error.message);
            });
    });
};

export default readParticipantInfoFromServer;   