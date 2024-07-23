import {RegistrationStatus, WorkshopInfoFromServer, WorkshopType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";



interface WorkshopInfoServerResult {
    workshopInfoFromServer:WorkshopInfoFromServer|null;
    errormessage:string|null;
}


const workshopInfoWithUserFromServer = (workshopId:string,accessToken:string|null):Promise<WorkshopInfoServerResult> => {
    return new Promise((resolve, reject) => {
        const readWorkshopInput = {
            workshopId: workshopId,
            accessToken: accessToken
        };
        fetch(ServerConfig.address + "/api/readWorkshop", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(readWorkshopInput)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            .then(json => {
                resolve({workshopInfoFromServer:json,errormessage:null})
            })
            .catch(error => {
                resolve({workshopInfoFromServer : null,errormessage:error.message});
            });
    })
}

export default workshopInfoWithUserFromServer